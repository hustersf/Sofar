package com.sofar.kmp.network.engine

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RealNetworkInterceptorChainTest {

  private fun request(
    url: String = "https://example.com/start",
    method: String = "GET",
    headers: Map<String, String> = emptyMap(),
    bodyString: String? = null
  ): NetworkRequest = NetworkRequest.Builder()
    .url(url)
    .method(method)
    .body(bodyString)
    .apply {
      headers.forEach { (key, value) -> header(key, value) }
    }
    .build()

  private fun response(
    statusCode: Int = 200,
    bodyString: String = "terminal",
    headers: Map<String, String> = emptyMap()
  ): NetworkResponse = NetworkResponse.Builder()
    .statusCode(statusCode)
    .body(bodyString)
    .apply {
      headers.forEach { (key, value) -> header(key, value) }
    }
    .build()

  @Test
  fun proceedsThroughInterceptorsInOrder() {
    val events = mutableListOf<String>()
    var terminalRequest: NetworkRequest? = null
    val interceptors = listOf(
      object : NetworkInterceptor {
        override fun intercept(chain: NetworkChain): NetworkResponse {
          events += "one-before"
          val response = chain.proceed(
            chain.request().newBuilder()
              .header("A", "1")
              .build()
          )
          events += "one-after"
          return response.newBuilder()
            .bodyString("${response.bodyString}|one")
            .build()
        }
      },
      object : NetworkInterceptor {
        override fun intercept(chain: NetworkChain): NetworkResponse {
          events += "two-before"
          val response = chain.proceed(
            chain.request().newBuilder()
              .header("B", "2")
              .build()
          )
          events += "two-after"
          return response.newBuilder()
            .bodyString("${response.bodyString}|two")
            .build()
        }
      }
    )
    val initialRequest = request()
    val chain = RealNetworkInterceptorChain(
      interceptors = interceptors,
      index = 0,
      request = initialRequest,
      requestBuilder = HttpRequestBuilder()
    ) { request ->
      terminalRequest = request
      response(bodyString = "terminal:${request.headers["A"]}:${request.headers["B"]}")
    }

    val response = chain.proceed(initialRequest)

    assertEquals(listOf("one-before", "two-before", "two-after", "one-after"), events)
    assertEquals("terminal:1:2|two|one", response.bodyString)
    assertEquals(mapOf("A" to "1", "B" to "2"), terminalRequest?.headers)
  }

  @Test
  fun multipleInterceptorsCanMutateEveryRequestAndResponsePart() {
    var terminalRequest: NetworkRequest? = null
    val interceptors = listOf(
      object : NetworkInterceptor {
        override fun intercept(chain: NetworkChain): NetworkResponse {
          val response = chain.proceed(
            chain.request().newBuilder()
              .url("https://example.com/one")
              .method("PUT")
              .body("body-one")
              .header("Keep", "keep")
              .header("Replace", "one")
              .build()
          )

          return response.newBuilder()
            .statusCode(210)
            .body("${response.bodyString}|one")
            .header("Response-One", "1")
            .header("Response-Replace", "one")
            .build()
        }
      },
      object : NetworkInterceptor {
        override fun intercept(chain: NetworkChain): NetworkResponse {
          val response = chain.proceed(
            chain.request().newBuilder()
              .url("https://example.com/two")
              .method("POST")
              .body("body-two")
              .header("Replace", "two")
              .header("Request-Two", "2")
              .removeHeader("Remove")
              .build()
          )

          return response.newBuilder()
            .statusCode(220)
            .body("${response.bodyString}|two")
            .header("Response-Two", "2")
            .header("Response-Replace", "two")
            .removeHeader("Response-Remove")
            .build()
        }
      }
    )
    val initialRequest = request(
      headers = mapOf(
        "Remove" to "remove",
        "Replace" to "origin"
      ),
      bodyString = "origin-body"
    )
    val chain = RealNetworkInterceptorChain(
      interceptors = interceptors,
      index = 0,
      request = initialRequest,
      requestBuilder = HttpRequestBuilder()
    ) { request ->
      terminalRequest = request
      response(
        statusCode = 200,
        bodyString = "terminal",
        headers = mapOf(
          "Response-Keep" to "keep",
          "Response-Replace" to "origin",
          "Response-Remove" to "remove"
        )
      )
    }

    val finalResponse = chain.proceed(initialRequest)

    assertEquals("https://example.com/two", terminalRequest?.url)
    assertEquals("POST", terminalRequest?.method)
    assertEquals("body-two", terminalRequest?.bodyString)
    assertEquals(
      mapOf(
        "Keep" to "keep",
        "Replace" to "two",
        "Request-Two" to "2"
      ),
      terminalRequest?.headers
    )
    assertEquals(210, finalResponse.statusCode)
    assertEquals("terminal|two|one", finalResponse.bodyString)
    assertEquals(
      mapOf(
        "Response-Keep" to "keep",
        "Response-Replace" to "one",
        "Response-Two" to "2",
        "Response-One" to "1"
      ),
      finalResponse.headers
    )
  }

  @Test
  fun throwsWhenInterceptorDoesNotCallProceed() {
    val interceptor = object : NetworkInterceptor {
      override fun intercept(chain: NetworkChain): NetworkResponse {
        return response(bodyString = "short-circuit")
      }
    }
    val initialRequest = request()
    val chain = RealNetworkInterceptorChain(
      interceptors = listOf(interceptor),
      index = 0,
      request = initialRequest,
      requestBuilder = HttpRequestBuilder()
    ) {
      response()
    }

    val error = assertFailsWith<IllegalStateException> {
      chain.proceed(initialRequest)
    }

    assertTrue(error.message.orEmpty().contains("must call proceed() exactly once"))
  }

  @Test
  fun throwsBeforeExecutingSecondProceedOnSameChain() {
    var terminalCalls = 0
    val interceptor = object : NetworkInterceptor {
      override fun intercept(chain: NetworkChain): NetworkResponse {
        val response = chain.proceed(chain.request())
        chain.proceed(chain.request())
        return response
      }
    }
    val initialRequest = request()
    val chain = RealNetworkInterceptorChain(
      interceptors = listOf(interceptor),
      index = 0,
      request = initialRequest,
      requestBuilder = HttpRequestBuilder()
    ) {
      terminalCalls++
      response()
    }

    val error = assertFailsWith<IllegalStateException> {
      chain.proceed(initialRequest)
    }

    assertTrue(error.message.orEmpty().contains("must call proceed() exactly once"))
    assertEquals(1, terminalCalls)
  }

  @Test
  fun appliesRequestMutationsToKtorBuilderWithoutClearingUnmentionedHeaders() {
    val builder = HttpRequestBuilder().apply {
      headers.append("Old", "stale")
      headers.append("Replace", "before")
      headers.append("Remove", "remove")
    }
    val interceptor = object : NetworkInterceptor {
      override fun intercept(chain: NetworkChain): NetworkResponse {
        return chain.proceed(
          chain.request().newBuilder()
            .url("https://example.com/changed")
            .method("POST")
            .body("changed-request-body")
            .header("Replace", "after")
            .header("New", "value")
            .removeHeader("Remove")
            .build()
        )
      }
    }
    val initialRequest = request(
      headers = mapOf(
        "Old" to "stale",
        "Replace" to "before",
        "Remove" to "remove"
      )
    )
    val chain = RealNetworkInterceptorChain(
      interceptors = listOf(interceptor),
      index = 0,
      request = initialRequest,
      requestBuilder = builder
    ) {
      response()
    }

    chain.proceed(initialRequest)

    val headers = builder.headers.build()
    assertEquals("POST", builder.method.value)
    assertEquals("https://example.com/changed", builder.url.buildString())
    assertEquals("stale", headers["Old"])
    assertEquals("after", headers["Replace"])
    assertEquals("value", headers["New"])
    assertNull(headers["Remove"])
  }

  @Test
  fun requestBuilderUsesMapHeaderSemantics() {
    val request = request(
      headers = mapOf(
        "Keep" to "keep",
        "Replace" to "before",
        "Remove" to "remove"
      ),
      bodyString = "origin-body"
    )

    val changed = request.newBuilder()
      .url("https://example.com/changed")
      .method("POST")
      .body("changed-body")
      .header("Replace", "after")
      .header("New", "new")
      .removeHeader("Remove")
      .build()

    assertEquals("https://example.com/changed", changed.url)
    assertEquals("POST", changed.method)
    assertEquals("changed-body", changed.bodyString)
    assertEquals(
      mapOf(
        "Keep" to "keep",
        "Replace" to "after",
        "New" to "new"
      ),
      changed.headers
    )
  }

  @Test
  fun responseBuilderUsesMapHeaderSemantics() {
    val response = response(
      bodyString = "origin",
      headers = mapOf(
        "Keep" to "keep",
        "Replace" to "before",
        "Remove" to "remove"
      )
    )

    val changed = response.newBuilder()
      .statusCode(201)
      .body("changed")
      .header("Replace", "after")
      .header("New", "new")
      .removeHeader("Remove")
      .build()

    assertEquals(201, changed.statusCode)
    assertEquals("changed", changed.bodyString)
    assertEquals(
      mapOf(
        "Keep" to "keep",
        "Replace" to "after",
        "New" to "new"
      ),
      changed.headers
    )
  }

  @Test
  fun pluginReplacesKtorResponseStatusHeadersAndBody() = runBlocking {
    val client = HttpClient(MockEngine) {
      engine {
        addHandler { request ->
          assertEquals("https://example.com/changed", request.url.toString())
          assertEquals(HttpMethod.Post, request.method)
          assertEquals("keep", request.headers["Keep"])
          assertEquals("two", request.headers["Replace"])
          assertEquals("2", request.headers["Request-Two"])
          assertNull(request.headers["Remove"])

          respond(
            content = "server-body",
            status = HttpStatusCode.Accepted,
            headers = headersOf(
              "Server-Keep" to listOf("keep"),
              "Response-Replace" to listOf("server"),
              "Response-Remove" to listOf("remove")
            )
          )
        }
      }

      install(
        createBusinessInterceptorPlugin(
          listOf(
            object : NetworkInterceptor {
              override fun intercept(chain: NetworkChain): NetworkResponse {
                val response = chain.proceed(
                  chain.request().newBuilder()
                    .url("https://example.com/intermediate")
                    .method("PUT")
                    .body("body-one")
                    .header("Keep", "keep")
                    .header("Replace", "one")
                    .build()
                )

                return response.newBuilder()
                  .statusCode(207)
                  .body("${response.bodyString}|one")
                  .header("Response-One", "1")
                  .header("Response-Replace", "one")
                  .build()
              }
            },
            object : NetworkInterceptor {
              override fun intercept(chain: NetworkChain): NetworkResponse {
                val response = chain.proceed(
                  chain.request().newBuilder()
                    .url("https://example.com/changed")
                    .method("POST")
                    .body("body-two")
                    .header("Replace", "two")
                    .header("Request-Two", "2")
                    .removeHeader("Remove")
                    .build()
                )

                return response.newBuilder()
                  .statusCode(206)
                  .body("${response.bodyString}|two")
                  .header("Response-Two", "2")
                  .header("Response-Replace", "two")
                  .removeHeader("Response-Remove")
                  .build()
              }
            }
          )
        )
      )
    }

    val ktorResponse = client.get("https://example.com/start") {
      headers.append("Remove", "remove")
      headers.append("Replace", "origin")
    }

    assertEquals(207, ktorResponse.status.value)
    assertEquals("keep", ktorResponse.headers["Server-Keep"])
    assertEquals("one", ktorResponse.headers["Response-Replace"])
    assertEquals("2", ktorResponse.headers["Response-Two"])
    assertEquals("1", ktorResponse.headers["Response-One"])
    assertNull(ktorResponse.headers["Response-Remove"])
    assertEquals("server-body|two|one", ktorResponse.bodyAsText())

    val businessResponse = assertNotNull(ktorResponse.businessInterceptorResponse())
    assertEquals(207, businessResponse.statusCode)
    assertEquals("server-body|two|one", businessResponse.bodyString)
    assertEquals("one", businessResponse.headers["Response-Replace"])

    client.close()
  }

  @Test
  fun bodyMutationShouldUseJsonAsDefaultContentType() = runBlocking {
    var actualContentType: ContentType? = null
    val client = HttpClient(MockEngine) {
      engine {
        addHandler { request ->
          val body = request.body as TextContent
          actualContentType = body.contentType
          respond(
            content = "ok",
            status = HttpStatusCode.OK
          )
        }
      }

      install(
        createBusinessInterceptorPlugin(
          listOf(
            object : NetworkInterceptor {
              override fun intercept(chain: NetworkChain): NetworkResponse {
                return chain.proceed(
                  chain.request()
                    .newBuilder()
                    .body("""{"name":"jack"}""")
                    .build()
                )
              }
            }
          )
        )
      )
    }

    client.post("https://example.com/test") {
      setBody("""{"name":"tom"}""")
    }

    assertEquals(
      ContentType.Application.Json,
      actualContentType
    )

    client.close()
  }

  @Test
  fun bodyMutationShouldUseCustomContentType() = runBlocking {
    var actualContentType: ContentType? = null
    var actualBody: String? = null
    val client = HttpClient(MockEngine) {
      engine {
        addHandler { request ->
          val body = request.body as TextContent
          actualContentType = body.contentType
          actualBody = body.text
          respond(
            content = "ok",
            status = HttpStatusCode.OK
          )
        }
      }

      install(
        createBusinessInterceptorPlugin(
          listOf(
            object : NetworkInterceptor {
              override fun intercept(chain: NetworkChain): NetworkResponse {
                return chain.proceed(
                  chain.request()
                    .newBuilder()
                    .body("a=1&b=2")
                    .contentType(
                      ContentType.Application.FormUrlEncoded.toString()
                    )
                    .build()
                )
              }
            }
          )
        )
      )
    }

    client.post("https://example.com/test") {
      setBody("""{"name":"tom"}""")
    }

    assertEquals(ContentType.Application.FormUrlEncoded, actualContentType)
    assertEquals("a=1&b=2", actualBody)

    client.close()
  }

  @Serializable
  data class User(val name: String)

  @Test
  fun interceptorShouldWorkWithContentNegotiation() = runBlocking {
    val client = HttpClient(MockEngine) {
      engine {
        addHandler {
          respond(
            content = """{"name":"tom"}""",
            status = HttpStatusCode.OK,
            headers = headersOf(
              HttpHeaders.ContentType,
              ContentType.Application.Json.toString()
            )
          )
        }
      }

      install(ContentNegotiation) {
        json()
      }

      install(
        createBusinessInterceptorPlugin(
          listOf(
            object : NetworkInterceptor {
              override fun intercept(chain: NetworkChain): NetworkResponse {
                return chain.proceed(chain.request())
              }
            }
          )
        )
      )
    }

    val result: User = client.get("https://example.com").body()

    assertEquals("tom", result.name)

    client.close()
  }

  @Test
  fun urlMutationShouldReplaceExistingQueryParameters() {
    val builder = HttpRequestBuilder().apply {
      url("https://example.com/users?page=1&pageSize=10")
    }

    val interceptors = listOf(
      object : NetworkInterceptor {
        override fun intercept(chain: NetworkChain): NetworkResponse {
          return chain.proceed(
            chain.request()
              .newBuilder()
              .url("https://example.com2/customers?page=2&pageSize=20")
              .build()
          )
        }
      },
      object : NetworkInterceptor {
        override fun intercept(chain: NetworkChain): NetworkResponse {
          return chain.proceed(chain.request())
        }
      }
    )

    val initialRequest = request(url = "https://example.com/users?page=1&pageSize=10")

    val chain = RealNetworkInterceptorChain(
      interceptors = interceptors,
      index = 0,
      request = initialRequest,
      requestBuilder = builder
    ) {
      response()
    }

    chain.proceed(initialRequest)

    assertEquals(
      "https://example.com2/customers?page=2&pageSize=20",
      builder.url.buildString()
    )
    assertEquals(
      listOf("2"),
      builder.url.parameters.getAll("page")
    )
    assertEquals(
      listOf("20"),
      builder.url.parameters.getAll("pageSize")
    )
  }
}