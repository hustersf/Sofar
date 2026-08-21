package com.sofar.network

import com.google.gson.Gson
import com.skydoves.retrofit.adapters.result.ResultCallAdapterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * 核心网络引擎：负责底层的 Retrofit 构建与 Service 实例化
 */
class NetworkEngine(
  val baseUrl: String,
  private val interceptors: List<Interceptor>,
  private val gson: Gson,
) {

  companion object {
    val sdkJson = Json {
      // 基础配置：忽略服务器返回的多余字段，防止解析崩溃
      ignoreUnknownKeys = true
      // 如果字段有默认值，解析时若服务器没传，则使用本地默认值
      coerceInputValues = true
      // 字段缺失时不抛出异常，自动解析为 null 属性
      explicitNulls = false
      // 允许宽松的 JSON 格式
      isLenient = true
    }
    private val contentType = "application/json".toMediaType()
  }

  // 使用线程安全的 Map 存储缓存实例
  private val serviceCache = ConcurrentHashMap<Class<*>, Any>()

  private val retrofit: Retrofit by lazy {
    val client = OkHttpClient.Builder().apply {
      interceptors.forEach { addInterceptor(it) }
    }.build()

    Retrofit.Builder()
      .baseUrl(baseUrl)
      .client(client)
      .addConverterFactory(ScalarsConverterFactory.create())
      .addConverterFactory(GsonConverterFactory.create(gson))
      .addConverterFactory(sdkJson.asConverterFactory(contentType))
      .addCallAdapterFactory(ResultCallAdapterFactory.create())
      .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
      .build()
  }

  /**
   * 生产 API 实例
   */
  @Suppress("UNCHECKED_CAST")
  fun <T : Any> create(serviceClass: Class<T>): T {
    return serviceCache.computeIfAbsent(serviceClass) {
      retrofit.create(serviceClass)
    } as T
  }
}
