package com.sofar.kmp.network.engine

import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readRawBytes
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpMethod
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.takeFrom
import io.ktor.util.AttributeKey
import io.ktor.util.date.GMTDate
import io.ktor.util.toMap
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

private const val HTTP_STATUS_OK = 200

/**
 * 暴露给外部 Android / iOS 接入方的统一只读请求快照。
 *
 * 对齐 OkHttp Request 的设计：对象不可变，只能通过 newBuilder() 复制后修改。
 */
class NetworkRequest internal constructor(
  builder: Builder
) {
  val url: String = builder.url
  val method: String = builder.method
  val headers: Map<String, String> = builder.headers.toMap()
  val bodyString: String? = builder.bodyString
  val contentType: String? = builder.contentType

  fun newBuilder(): Builder = Builder(this)

  /**
   * OkHttp Request.Builder 风格的请求构造器。
   *
   * header(name, value): 同名覆盖，不影响其它 Header。
   * removeHeader(name): 仅移除指定 Header。
   */
  class Builder {
    internal var url: String = ""
    internal var method: String = "GET"
    internal val headers: MutableMap<String, String> = mutableMapOf()
    internal var bodyString: String? = null
    internal var contentType: String? = null

    constructor()

    internal constructor(request: NetworkRequest) {
      url = request.url
      method = request.method
      headers.putAll(request.headers)
      bodyString = request.bodyString
      contentType = request.contentType
    }

    fun url(url: String): Builder = apply {
      this.url = url
    }

    fun method(method: String): Builder = apply {
      this.method = method
    }

    fun bodyString(bodyString: String?): Builder = apply {
      this.bodyString = bodyString
    }

    fun body(bodyString: String?): Builder = bodyString(bodyString)

    fun contentType(contentType: String?): Builder = apply {
      this.contentType = contentType
    }

    fun header(name: String, value: String): Builder = apply {
      headers[name] = value
    }

    fun removeHeader(name: String): Builder = apply {
      headers.remove(name)
    }

    fun build(): NetworkRequest {
      return NetworkRequest(this)
    }
  }
}

/**
 * 暴露给外部 Android / iOS 接入方的统一只读响应快照。
 *
 * 对齐 OkHttp Response 的设计：对象不可变，只能通过 newBuilder() 复制后修改。
 */
class NetworkResponse internal constructor(
  builder: Builder
) {
  val statusCode: Int = builder.statusCode
  val bodyString: String = builder.bodyString
  val headers: Map<String, String> = builder.headers.toMap()

  fun newBuilder(): Builder = Builder(this)

  /**
   * OkHttp Response.Builder 风格的响应构造器。
   *
   * header(name, value): 同名覆盖，不影响其它 Header。
   * removeHeader(name): 仅移除指定 Header。
   */
  class Builder {
    internal var statusCode: Int = HTTP_STATUS_OK
    internal var bodyString: String = ""
    internal val headers: MutableMap<String, String> = mutableMapOf()

    constructor()

    internal constructor(response: NetworkResponse) {
      statusCode = response.statusCode
      bodyString = response.bodyString
      headers.putAll(response.headers)
    }

    fun statusCode(statusCode: Int): Builder = apply {
      this.statusCode = statusCode
    }

    fun bodyString(bodyString: String): Builder = apply {
      this.bodyString = bodyString
    }

    fun body(bodyString: String): Builder = bodyString(bodyString)

    fun header(name: String, value: String): Builder = apply {
      headers[name] = value
    }

    fun removeHeader(name: String): Builder = apply {
      headers.remove(name)
    }

    fun build(): NetworkResponse {
      return NetworkResponse(this)
    }
  }
}

/**
 * 接入方需要实现的双向拦截器接口（Android 与 iOS 原生共用）
 */
interface NetworkInterceptor {
  fun intercept(chain: NetworkChain): NetworkResponse
}

/**
 * 驱动拦截器串联运行的职责链接口
 */
interface NetworkChain {
  fun request(): NetworkRequest
  fun proceed(request: NetworkRequest): NetworkResponse
}

private val BusinessInterceptorResponseAttributeKey =
  AttributeKey<NetworkResponse>("BusinessInterceptorResponse")

fun HttpResponse.businessInterceptorResponse(): NetworkResponse? =
  call.attributes.getOrNull(BusinessInterceptorResponseAttributeKey)

/**
 * 将业务拦截器最终返回的 NetworkResponse 适配成 Ktor HttpResponse。
 *
 * Send hook 会在拦截器链完整回退后返回 BusinessHttpClientCall，
 * 让后续读取 response.status / response.headers / response.rawContent 时都能看到最终业务响应。
 */
@OptIn(InternalAPI::class)
private class BusinessHttpResponse(
  origin: HttpResponse,
  private val businessResponse: NetworkResponse
) : HttpResponse() {
  override val call: HttpClientCall = origin.call
  override val coroutineContext: CoroutineContext = origin.coroutineContext
  override val status: HttpStatusCode = HttpStatusCode.fromValue(businessResponse.statusCode)
  override val version: HttpProtocolVersion = origin.version
  override val requestTime: GMTDate = origin.requestTime
  override val responseTime: GMTDate = origin.responseTime
  override val headers: Headers = Headers.build {
    businessResponse.headers.forEach { (key, value) ->
      append(key, value)
    }
  }
  override val rawContent: ByteReadChannel =
    ByteReadChannel(businessResponse.bodyString.encodeToByteArray())
}

private class BusinessHttpClientCall(
  origin: HttpClientCall,
  businessResponse: NetworkResponse
) : HttpClientCall(origin.client) {
  init {
    request = origin.request
    response = BusinessHttpResponse(origin.response, businessResponse)
  }
}

private fun HttpRequestBuilder.applyNetworkRequest(request: NetworkRequest) {
  url.parameters.clear()
  url.takeFrom(request.url)
  method = HttpMethod.parse(request.method)

  // NetworkRequest 是完整不可变快照：快照中不存在的 Header 应从底层物理请求移除。
  headers.build().names().forEach { key ->
    if (key !in request.headers) {
      headers.remove(key)
    }
  }

  // 对齐 OkHttp Request.Builder.header(name, value)：同名覆盖。
  request.headers.forEach { (key, value) ->
    headers.remove(key)
    headers.append(key, value)
  }

  request.bodyString?.let { body ->
    val contentType = request.contentType?.let(ContentType::parse) ?: ContentType.Application.Json
    setBody(TextContent(text = body, contentType = contentType))
  }
}

/**
 * 参考 OkHttp RealInterceptorChain 的真实职责链实现：
 * - 每次 proceed 都创建 index + 1 的下一段链，避免共享可变游标
 * - 当前链记录 calls 次数，用于约束拦截器必须且只能调用一次 proceed
 * - 拦截器返回 null 在 Kotlin 类型层面不可达，因此只校验职责链调用次数
 */
internal class RealNetworkInterceptorChain(
  private val interceptors: List<NetworkInterceptor>,
  private val index: Int,
  private val request: NetworkRequest,
  private val requestBuilder: HttpRequestBuilder,
  private val executeNetworkCall: (NetworkRequest) -> NetworkResponse
) : NetworkChain {

  private var calls: Int = 0

  override fun request(): NetworkRequest = request

  override fun proceed(request: NetworkRequest): NetworkResponse {
    calls++
    check(calls == 1) {
      "network interceptor ${interceptors.getOrNull(index - 1) ?: this} must call proceed() exactly once"
    }

    if (index >= interceptors.size) {
      applyRequestToKtorBuilder(request)
      return executeNetworkCall(request)
    }

    val next = RealNetworkInterceptorChain(
      interceptors = interceptors,
      index = index + 1,
      request = request,
      requestBuilder = requestBuilder,
      executeNetworkCall = executeNetworkCall
    )
    val interceptor = interceptors[index]
    val response = interceptor.intercept(next)

    check(next.calls == 1) {
      "network interceptor $interceptor must call proceed() exactly once"
    }

    return response
  }

  private fun applyRequestToKtorBuilder(request: NetworkRequest) {
    requestBuilder.applyNetworkRequest(request)
  }
}

/**
 * Ktor 核心桥接插件生成函数（仅供 NetworkEngine 内部挂载）
 */
internal fun createBusinessInterceptorPlugin(
  interceptors: List<NetworkInterceptor>
) = createClientPlugin("BusinessInterceptorPlugin") {
  // Send hook 发生在 engine 发送前，request 修改会真实影响底层请求。
  on(Send) { requestBuilder: HttpRequestBuilder ->
    val sender = this

    // 提取并序列化当前由上游配置注入的物理请求头
    val currentKtorHeaders =
      requestBuilder.headers.build().toMap().mapValues { it.value.joinToString(",") }

    // 封装初始请求参数
    val initialNetworkRequest = NetworkRequest.Builder()
      .url(requestBuilder.url.buildString())
      .method(requestBuilder.method.value)
      .apply {
        currentKtorHeaders.forEach { (key, value) ->
          header(key, value)
        }
      }
      .build()
    var executedCall: HttpClientCall? = null

    val chain = RealNetworkInterceptorChain(
      interceptors = interceptors,
      index = 0,
      request = initialNetworkRequest,
      requestBuilder = requestBuilder
    ) {
      // 到达职责链终点，执行物理联网
      val currentCall = runBlocking { sender.proceed(requestBuilder) }
      executedCall = currentCall

      // 转换泛型上下文为主体的物理调用句柄
      val ktorResponse: HttpResponse = currentCall.response

      // 读取底层网卡流入的二进制数据
      val bytes: ByteArray = runBlocking { ktorResponse.readRawBytes() }
      val decodedBody = bytes.decodeToString()
      val responseHeaders = ktorResponse.headers.toMap().mapValues { it.value.joinToString(",") }

      val networkResponse = NetworkResponse.Builder()
        .statusCode(ktorResponse.status.value)
        .bodyString(decodedBody)
        .apply {
          responseHeaders.forEach { (key, value) ->
            header(key, value)
          }
        }
        .build()
      currentCall.attributes.put(BusinessInterceptorResponseAttributeKey, networkResponse)

      networkResponse
    }

    // 驱动拦截器链逻辑
    val finalNetworkResponse = chain.proceed(initialNetworkRequest)

    val currentCall = executedCall ?: error("Business interceptor did not execute network call")
    currentCall.attributes.put(BusinessInterceptorResponseAttributeKey, finalNetworkResponse)
    BusinessHttpClientCall(currentCall, finalNetworkResponse)
  }
}
