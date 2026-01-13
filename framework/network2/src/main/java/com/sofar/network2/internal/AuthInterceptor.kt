package com.sofar.network2.internal

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val token = TokenManager.getAccessToken()
    val requestBuilder = chain.request().newBuilder()

    if (!token.isNullOrEmpty()) {
      requestBuilder.header("Authorization", "AccessToken=$token")
    }

    return chain.proceed(requestBuilder.build())
  }
}