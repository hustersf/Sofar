package com.sofar.kmp.network.openapi.internal

import com.sofar.kmp.network.openapi.AuthData
import com.sofar.kmp.network.openapi.SdkConfig
import com.sofar.kmp.network.openapi.TokenManager
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** 外部模式凭证 */
class OpenAuthData(val accessToken: String) : AuthData

internal class OpenTokenManager(
  private val config: SdkConfig
) : TokenManager {
  companion object {
    const val ERROR_CODE_TOKEN_EXPIRED = -44112
    val TOKEN_INVALID_CODES = setOf(ERROR_CODE_TOKEN_EXPIRED)
  }

  private val mutex = Mutex()

  override fun isExpired(response: HttpResponse, errorCode: Int?): Boolean {
    return response.status == HttpStatusCode.Unauthorized || TOKEN_INVALID_CODES.contains(errorCode)
  }

  override fun getCurrentToken(): String? {
    return config.accessToken
  }

  override suspend fun refreshAndGet(oldToken: String?): String? = mutex.withLock {
    if (config.accessToken != null && config.accessToken != oldToken) {
      return@withLock config.accessToken
    }

    val data = config.tokenFetcher?.fetch() as? OpenAuthData
    return@withLock data?.accessToken?.also {
      config.accessToken = it
    }
  }
}
