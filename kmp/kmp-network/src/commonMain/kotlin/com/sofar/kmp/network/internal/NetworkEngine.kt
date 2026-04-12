package com.sofar.kmp.network.internal

import com.sofar.kmp.network.configureCustomCertificate
import com.sofar.kmp.network.configureTrustAll
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
  private val trustAll: Boolean = false,
  private val trustedCert: String? = null,
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
      // 核心序列化配置 (替代 asConverterFactory)
      install(ContentNegotiation) {
        json(sdkJson)
      }

      // 基础请求配置 (替代 baseUrl)
      defaultRequest {
        url(baseUrl)
        contentType(ContentType.Application.Json)
      }

      // 注入外部配置（如日志、自定义 Header 等，替代拦截器）
      httpClientConfig(this)

      // 证书策略
      when {
        // 信任所有
        trustAll -> {
          configureTrustAll()
        }
        // 判断是否有自定义证书：生产模式，实现“手动信任”
        !trustedCert.isNullOrBlank() -> {
          configureCustomCertificate(trustedCert)
        }
      }

      // 异常处理 (类似 ResultCallAdapter)
      expectSuccess = false // 允许非 200 状态码进入 body 处理逻辑
    }
  }
}
