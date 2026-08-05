package com.sofar.kmp.network.openapi.internal

import com.sofar.kmp.network.openapi.SdkConfig
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header

private const val HEADER_AUTH = "Authorization"
private const val TOKEN_PREFIX = "AccessToken="

/**
 * 封装扩展函数：一键安装认证体系
 */
fun HttpClientConfig<*>.installOpenApiAuth(config: SdkConfig) {
  // 注入 Token
  defaultRequest {
    config.accessToken?.let {
      header(HEADER_AUTH, "$TOKEN_PREFIX$it")
    }
  }
}
