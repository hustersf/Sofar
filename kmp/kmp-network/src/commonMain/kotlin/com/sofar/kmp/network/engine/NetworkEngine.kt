package com.sofar.kmp.network.engine

import com.sofar.kmp.network.configureCustomCertificate
import com.sofar.kmp.network.configureTrustAll
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * 网络请求驱动引擎
 * 负责 Ktor [HttpClient] 的具体实例化，包含序列化配置、超时拦截、日志打印及 SSL 证书策略。
 */
class NetworkEngine(
  private val config: NetworkConfig,
  // Ktor 使用配置函数(Plugin)替代了 OkHttp 的 Interceptor
  private val httpClientConfig: HttpClientConfig<*>.() -> Unit = {}
) {

  companion object {
    val sdkJson = Json {
      ignoreUnknownKeys = true
      coerceInputValues = true
      isLenient = true
      encodeDefaults = true
    }
    const val SOCKET_TIMEOUT_FACTOR = 3L
  }

  val httpClient: HttpClient by lazy {
    HttpClient {
      // 核心序列化配置 (替代 asConverterFactory)
      install(ContentNegotiation) {
        json(sdkJson)
      }

      // 设置超时
      install(HttpTimeout) {
        connectTimeoutMillis = config.connectTimeout
        socketTimeoutMillis = config.connectTimeout * SOCKET_TIMEOUT_FACTOR
      }

      // 日志
      if (config.debugMode) {
        install(Logging) {
          logger = Logger.SIMPLE
          level = LogLevel.ALL
        }
      }

      // 基础请求配置 (替代 baseUrl)
      defaultRequest {
        url(config.baseUrl)
        contentType(ContentType.Application.Json)
      }

      // 注入外部配置（如自定义 Header 等，替代拦截器）
      httpClientConfig(this)

      // 注入接入方自定义拦截器插件
      if (config.interceptors.isNotEmpty()) {
        val businessPlugin = createBusinessInterceptorPlugin(config.interceptors)
        install(businessPlugin)
      }

      // 证书策略
      when {
        // 信任所有
        config.trustAll -> {
          configureTrustAll()
        }
        // 判断是否有自定义证书：生产模式，实现“手动信任”
        !config.trustedCert.isNullOrBlank() -> {
          configureCustomCertificate(config.trustedCert)
        }
      }

      // 异常处理 (类似 ResultCallAdapter)
      expectSuccess = false // 允许非 200 状态码进入 body 处理逻辑
    }
  }
}