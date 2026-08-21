package com.sofar.network

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor

class ApiClient @JvmOverloads constructor(
  private val baseUrl: String,
  private val customInterceptors: List<Interceptor> = emptyList(),
  private val gson: Gson = Gson(),
  private val debugMode: Boolean = false
) {

  private val engine: NetworkEngine by lazy {
    val interceptors = mutableListOf<Interceptor>().apply {
      addAll(customInterceptors)
      if (debugMode) {
        add(HttpLoggingInterceptor().apply {
          level = HttpLoggingInterceptor.Level.BODY
        })
      }
    }

    NetworkEngine(
      baseUrl = baseUrl,
      interceptors = interceptors,
      gson = gson
    )
  }

  fun <T : Any> create(serviceClass: Class<T>): T {
    return engine.create(serviceClass)
  }

  inline fun <reified T : Any> create(): T = create(T::class.java)
}
