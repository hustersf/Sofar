package com.sofar.network2.core

import android.content.Context
import com.sofar.network2.api.ApiService
import com.sofar.network2.api.AuthService
import com.sofar.network2.internal.AuthInterceptor
import com.sofar.network2.internal.MockTokenInterceptor
import com.sofar.network2.internal.NetworkEngine
import com.sofar.network2.internal.SdkInternal
import com.sofar.network2.internal.TokenRetryInterceptor
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor

class OpenApiClient private constructor() {

  private lateinit var engine: NetworkEngine

  companion object {
    @JvmStatic
    private val instance: OpenApiClient by lazy {
      OpenApiClient()
    }

    @JvmStatic
    fun get(): OpenApiClient = instance
  }

  /**
   * SDK 初始化入口
   */
  fun init(context: Context, config: SdkConfig = SdkConfig.build()) {
    // 参数注入
    SdkInternal.inject(context, config)

    // 业务相关的拦截器
    val interceptors = mutableListOf<Interceptor>().apply {
      // 最外层：监控 Token 失效并自动重试
      add(TokenRetryInterceptor(NetworkEngine.sdkJson))
      // 注入层：确保重试请求能拿到最新 Token
      add(AuthInterceptor())
      // 监控层：Debug 模式下打印日志
      if (config.debugMode) {
        add(HttpLoggingInterceptor().apply {
          level = HttpLoggingInterceptor.Level.BODY
        })
        // 模拟层：作为请求终点
        add(MockTokenInterceptor())
      }
    }

    engine = NetworkEngine(
      baseUrl = config.baseUrl,
      interceptors = interceptors
    )
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