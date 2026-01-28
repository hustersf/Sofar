package com.sofar.kmp.network.core

import com.sofar.kmp.network.api.AuthApi
import com.sofar.kmp.network.api.BannerApi
import com.sofar.kmp.network.internal.NetworkEngine
import com.sofar.kmp.network.internal.SdkInternal
import com.sofar.kmp.network.internal.installOpenApiAuth
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import kotlin.jvm.JvmStatic

class OpenApiClient private constructor() {

  companion object {
    private val instance: OpenApiClient by lazy { OpenApiClient() }

    @JvmStatic
    fun get(): OpenApiClient = instance
  }

  @PublishedApi
  internal lateinit var engine: NetworkEngine

  fun init(config: SdkConfig = SdkConfig.build()) {
    SdkInternal.inject(config)
    engine = NetworkEngine(
      baseUrl = config.baseUrl,
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

  val auth: AuthApi by lazy { AuthApi(engine) }
  val banner: BannerApi by lazy { BannerApi(engine) }
}

/**
 * 专门为 iOS/Swift 提供的快捷访问函数
 */
fun getOpenApiClient(): OpenApiClient = OpenApiClient.get()