package com.sofar.network.openapi

import android.content.Context
import com.sofar.network.ApiClient
import com.sofar.network.NetworkEngine
import com.sofar.network.openapi.api.ApiService
import com.sofar.network.openapi.api.AuthService
import com.sofar.network.openapi.auth.AuthInterceptor
import com.sofar.network.openapi.auth.TokenManager
import com.sofar.network.openapi.auth.TokenRetryInterceptor

class OpenApiClient(
  private val context: Context,
  val config: SdkConfig = SdkConfig.build()
) {
  val tokenManager: TokenManager by lazy {
    TokenManager(config) { authApiService() }.apply {
      setProvider(config.tokenProvider)
    }
  }

  private val apiClient: ApiClient by lazy {
    // 业务相关的拦截器
    val interceptors = listOf(
      // 最外层：监控 Token 失效并自动重试
      TokenRetryInterceptor(tokenManager, NetworkEngine.sdkJson),
      // 注入层：确保重试请求能拿到最新 Token
      AuthInterceptor(tokenManager)
    )

    ApiClient(
      baseUrl = config.baseUrl,
      customInterceptors = interceptors,
      gson = config.gson,
      debugMode = config.debugMode
    )
  }

  fun newBuilder(newBaseUrl: String): OpenApiClient {
    return OpenApiClient(context, config.mutateBaseUrl(newBaseUrl))
  }

  fun <T : Any> create(serviceClass: Class<T>): T {
    return apiClient.create(serviceClass)
  }

  inline fun <reified T : Any> create(): T = create(T::class.java)

  val apiService: ApiService by lazy { create() }

  fun authApiService(): AuthService = create()
}

// --- 以下是扩展函数,方便业务调用 ---
inline fun <reified S : Any> OpenApiClient.on(): S = create<S>()
