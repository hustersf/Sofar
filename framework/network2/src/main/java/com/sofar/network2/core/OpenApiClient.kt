package com.sofar.network2.core

import android.content.Context
import com.skydoves.retrofit.adapters.result.ResultCallAdapterFactory
import com.sofar.network2.api.ApiService
import com.sofar.network2.api.AuthService
import com.sofar.network2.internal.AuthInterceptor
import com.sofar.network2.internal.MockTokenInterceptor
import com.sofar.network2.internal.SdkInternal
import com.sofar.network2.internal.TokenRetryInterceptor
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class OpenApiClient private constructor() {

  private lateinit var retrofit: Retrofit
  private lateinit var noAuthRetrofit: Retrofit
  private val sdkJson = Json {
    // 基础配置：忽略服务器返回的多余字段，防止解析崩溃
    ignoreUnknownKeys = true
    // 如果字段有默认值，解析时若服务器没传，则使用本地默认值
    coerceInputValues = true
    // 允许宽松的 JSON 格式
    isLenient = true
  }

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
    SdkInternal.inject(context, config)
    val baseClient = OkHttpClient.Builder().build()

    noAuthRetrofit = createRetrofit(baseClient, config) {
      // 刷新接口只需要最简单的：日志 -> Mock
      if (config.debugMode) {
        addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        addInterceptor(MockTokenInterceptor())
      }
    }

    retrofit = createRetrofit(baseClient, config) {
      // 1. 最外层：监控 401 并自动重试
      addInterceptor(TokenRetryInterceptor(sdkJson))
      // 2. 注入层：确保重试请求能拿到最新 Token
      addInterceptor(AuthInterceptor())
      // 3. 监控层：看最终注入后的 Header
      if (config.debugMode) {
        addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        // 4. 模拟层：作为请求终点
        addInterceptor(MockTokenInterceptor())
      }
    }
  }

  private fun createRetrofit(
    baseClient: OkHttpClient,
    config: SdkConfig,
    interceptorBlock: OkHttpClient.Builder.() -> Unit,
  ): Retrofit {
    val contentType = "application/json".toMediaType()

    val client = baseClient.newBuilder()
      .apply(interceptorBlock) // 注入各自特有的拦截器
      .build()

    return Retrofit.Builder()
      .baseUrl(config.baseUrl)
      .client(client)
      .addConverterFactory(ScalarsConverterFactory.create())
      .addConverterFactory(sdkJson.asConverterFactory(contentType))
      .addCallAdapterFactory(ResultCallAdapterFactory.create())
      .build()
  }

  fun <T : Any> create(serviceClass: Class<T>): T {
    return retrofit.create(serviceClass)
  }

  inline fun <reified T : Any> create(): T = create(T::class.java)

  val apiService: ApiService by lazy { create<ApiService>() }

  fun authApiService(): AuthService {
    return noAuthRetrofit.create(AuthService::class.java)
  }
}

// --- 以下是扩展函数,方便业务调用 ---
inline fun <reified S : Any> OpenApiClient.on(): S = create<S>()