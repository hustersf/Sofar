package com.sofar.kmp.network.internal

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header


private const val HEADER_AUTH = "Authorization"
private const val TOKEN_PREFIX = "AccessToken="
const val SOCKET_TIMEOUT_FACTOR = 3L

/**
 * 封装扩展函数：一键安装认证体系
 */
fun HttpClientConfig<*>.installOpenApiAuth() {
  // 注入 Token
  defaultRequest {
    SdkInternal.config.accessToken?.let {
      header(HEADER_AUTH, "$TOKEN_PREFIX$it")
    }
  }

  // 设置超时
  install(HttpTimeout) {
    connectTimeoutMillis = SdkInternal.config.connectTimeout
    socketTimeoutMillis = SdkInternal.config.connectTimeout * SOCKET_TIMEOUT_FACTOR
  }
}
