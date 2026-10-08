package com.sofar.kmp.demo.feature.network.service

import com.sofar.kmp.network.engine.NetworkConfig
import com.sofar.kmp.network.engine.NetworkEngine
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable

@Serializable
data class TestRequest(
  val id: String,
  val content: String
)

@Serializable
data class HttpBinResponse(
  val json: TestRequest? = null,
  val url: String = ""
)

class HttpBinService(
  private val engine: NetworkEngine = NetworkEngine(
    config = NetworkConfig.Builder()
      .setBaseUrl("https://httpbin.org/")
      .setDebugMode(true)
      .build()
  )
) {

  fun testPost(body: TestRequest): Flow<HttpBinResponse> = flow {
    val response = engine.httpClient.post("post") {
      setBody(body)
    }.body<HttpBinResponse>()
    emit(response)
  }
}
