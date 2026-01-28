package com.sofar.kmp.network.internal

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpStatusCode

private const val HEADER_AUTH = "Authorization"
private const val TOKEN_PREFIX = "AccessToken="

/**
 * 封装扩展函数：一键安装认证体系
 */
fun HttpClientConfig<*>.installOpenApiAuth() {

  // 1.注入 Token
  defaultRequest {
    val token = TokenManager.getAccessToken()
    if (!token.isNullOrEmpty()) {
      header(HEADER_AUTH, "${TOKEN_PREFIX}$token")
    }
  }

  // 2.验证标准 HTTP 状态码（处理标准 401）
  HttpResponseValidator {
    validateResponse { response ->
      if (response.status == HttpStatusCode.Unauthorized) {
        throw TokenException(response.status.value, "HTTP Unauthorized")
      }
    }
  }

  // 3.设置超时
  install(HttpTimeout) {
    connectTimeoutMillis = SdkInternal.config.connectTimeout
    socketTimeoutMillis = SdkInternal.config.connectTimeout * 3
  }
}