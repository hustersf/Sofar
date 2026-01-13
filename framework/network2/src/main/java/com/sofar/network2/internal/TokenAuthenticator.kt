package com.sofar.network2.internal

import kotlinx.coroutines.runBlocking
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.Authenticator

/**
 * 处理 Http 标准 401 未鉴权
 * [okhttp3.OkHttpClient.authenticator]
 */
class TokenAuthenticator : Authenticator {

  override fun authenticate(route: Route?, response: Response): Request? {
    val oldToken = response.request.header("Authorization")
      ?.removePrefix("AccessToken=")

    val newToken = runBlocking {
      TokenManager.refreshAndGet(oldToken)
    }
    if (newToken != null) {
      return response.request.newBuilder()
        .header("Authorization", "AccessToken=$newToken")
        .build()
    }

    return null
  }
}