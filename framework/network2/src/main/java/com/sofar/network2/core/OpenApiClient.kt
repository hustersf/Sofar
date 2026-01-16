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
import java.util.concurrent.ConcurrentHashMap

class OpenApiClient private constructor() {

  private lateinit var retrofit: Retrofit
  private val sdkJson = Json {
    // 基础配置：忽略服务器返回的多余字段，防止解析崩溃
    ignoreUnknownKeys = true
    // 如果字段有默认值，解析时若服务器没传，则使用本地默认值
    coerceInputValues = true
    // 允许宽松的 JSON 格式
    isLenient = true
  }

  // 使用线程安全的 Map 存储缓存实例
  private val serviceCache = ConcurrentHashMap<Class<*>, Any>()

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
    serviceCache.clear()
    // 参数注入
    SdkInternal.inject(context, config)

    // 构建 OkHttpClient 实例
    val contentType = "application/json".toMediaType()
    val client = OkHttpClient.Builder().apply {
      // 最外层：监控 Token 失效并自动重试
      addInterceptor(TokenRetryInterceptor(sdkJson))
      // 注入层：确保重试请求能拿到最新 Token
      addInterceptor(AuthInterceptor())
      // 监控层：Debug 模式下打印日志
      if (config.debugMode) {
        addInterceptor(HttpLoggingInterceptor().apply {
          level = HttpLoggingInterceptor.Level.BODY
        })
        // 模拟层：作为请求终点
        addInterceptor(MockTokenInterceptor())
      }
    }.build()

    // 构建 Retrofit 实例
    retrofit = Retrofit.Builder()
      .baseUrl(config.baseUrl)
      .client(client)
      .addConverterFactory(ScalarsConverterFactory.create())
      .addConverterFactory(sdkJson.asConverterFactory(contentType))
      .addCallAdapterFactory(ResultCallAdapterFactory.create())
      .build()
  }

  @Suppress("UNCHECKED_CAST")
  fun <T : Any> create(serviceClass: Class<T>): T {
    return serviceCache.computeIfAbsent(serviceClass) {
      retrofit.create(serviceClass)
    } as T
  }

  inline fun <reified T : Any> create(): T = create(T::class.java)

  val apiService: ApiService by lazy { create<ApiService>() }

  fun authApiService(): AuthService {
    return retrofit.create(AuthService::class.java)
  }
}

// --- 以下是扩展函数,方便业务调用 ---
inline fun <reified S : Any> OpenApiClient.on(): S = create<S>()