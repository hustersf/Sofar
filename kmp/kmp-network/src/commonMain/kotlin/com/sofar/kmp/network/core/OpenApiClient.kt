package com.sofar.kmp.network.core

import com.sofar.kmp.network.internal.NetworkEngine
import com.sofar.kmp.network.internal.OpenTokenManager
import com.sofar.kmp.network.internal.SdkInternal
import com.sofar.kmp.network.internal.installOpenApiAuth
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import kotlin.jvm.JvmStatic

class OpenApiClient private constructor() : ApiProvider() {

  companion object {
    private val instance: OpenApiClient by lazy { OpenApiClient() }

    @JvmStatic
    fun get(): OpenApiClient = instance
  }

  fun init(config: SdkConfig = SdkConfig.build()) {
    SdkInternal.inject(config)
    currentTokenManager = OpenTokenManager(config)
    engine = NetworkEngine(
      baseUrl = config.baseUrl,
      trustAll = config.trustAll,
      trustedCert = config.trustedCert,
      httpClientConfig = { clientConfig ->
        // 监控层：Debug 模式下打印日志 (替代 HttpLoggingInterceptor)
        if (config.debugMode) {
          clientConfig.install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
          }
        }
        clientConfig.installOpenApiAuth()
      }
    )
  }
}

/**
 * 专门为 iOS/Swift 提供的快捷访问函数
 */
fun getOpenApiClient(): OpenApiClient = OpenApiClient.get()
