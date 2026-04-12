package com.sofar.network2.core

import android.content.Context
import com.sofar.network2.api.ApiService
import com.sofar.network2.api.AuthService
import com.sofar.network2.internal.AuthInterceptor
import com.sofar.network2.internal.NetworkEngine
import com.sofar.network2.internal.TokenManager
import com.sofar.network2.internal.TokenRetryInterceptor
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor

class OpenApiClient(
  private val context: Context,
  val config: SdkConfig = SdkConfig.build()
) {
  val tokenManager: TokenManager by lazy {
    TokenManager(config) { authApiService() }.apply {
      setProvider(config.tokenProvider)
    }
  }

  private val engine: NetworkEngine by lazy {
    // 业务相关的拦截器
    val interceptors = mutableListOf<Interceptor>().apply {
      // 最外层：监控 Token 失效并自动重试
      add(TokenRetryInterceptor(tokenManager, NetworkEngine.sdkJson))
      // 注入层：确保重试请求能拿到最新 Token
      add(AuthInterceptor(tokenManager))
      // 监控层：Debug 模式下打印日志
      if (config.debugMode) {
        add(HttpLoggingInterceptor().apply {
          level = HttpLoggingInterceptor.Level.BODY
        })
      }
    }

    NetworkEngine(
      baseUrl = config.baseUrl,
      interceptors = interceptors
    )
  }

  fun newBuilder(newBaseUrl: String): OpenApiClient {
    return OpenApiClient(context, config.copy(baseUrl = newBaseUrl))
  }

  fun <T : Any> create(serviceClass: Class<T>): T {
    return engine.create(serviceClass)
  }

  inline fun <reified T : Any> create(): T = create(T::class.java)

  val apiService: ApiService by lazy { create() }

  fun authApiService(): AuthService = create()
}

// --- 以下是扩展函数,方便业务调用 ---
inline fun <reified S : Any> OpenApiClient.on(): S = create<S>()