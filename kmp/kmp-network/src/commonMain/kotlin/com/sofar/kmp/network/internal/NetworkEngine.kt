package com.sofar.kmp.network.internal

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class NetworkEngine(
  private val baseUrl: String,
  // Ktor 使用配置函数(Plugin)替代了 OkHttp 的 Interceptor
  private val httpClientConfig: (HttpClientConfig<*>) -> Unit = {}
) {

  companion object {
    val sdkJson = Json {
      ignoreUnknownKeys = true
      coerceInputValues = true
      isLenient = true
      encodeDefaults = true
    }
  }

  val httpClient: HttpClient by lazy {
    HttpClient {
      // 1. 核心序列化配置 (替代 asConverterFactory)
      install(ContentNegotiation) {
        json(sdkJson)
      }

      // 2. 基础请求配置 (替代 baseUrl)
      defaultRequest {
        url(baseUrl)
        contentType(ContentType.Application.Json)
      }

      // 3. 注入外部配置（如日志、自定义 Header 等，替代拦截器）
      httpClientConfig(this)

      // 4. 异常处理 (类似 ResultCallAdapter)
      expectSuccess = false // 允许非 200 状态码进入 body 处理逻辑
    }
  }
}