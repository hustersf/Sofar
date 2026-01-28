package com.sofar.kmp.network.internal

import com.sofar.kmp.network.core.OpenApiClient
import com.sofar.kmp.network.core.TokenProvider
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object TokenManager {
  private val mutex = Mutex()
  private var provider: TokenProvider = MemoryTokenProvider()

  fun setProvider(p: TokenProvider?) {
    if (p != null) {
      this.provider = p
    }
  }

  fun getAccessToken(): String? = provider.getAccessToken()

  suspend fun refreshAndGet(oldToken: String?): String? = mutex.withLock {
    val p = provider

    val current = p.getAccessToken()
    // 双重检查逻辑
    if (current != null && current != oldToken) return current

    // 获取 refreshToken
    val rt = p.getRefreshToken()
    val authApi = OpenApiClient.get().auth

    val result = if (rt.isNullOrEmpty()) {
      // 策略 A: 初始登录
      authApi.getToken(SdkInternal.config.apiKey, SdkInternal.config.apiSecret)
    } else {
      // 策略 B: 刷新令牌
      authApi.refreshToken(SdkInternal.config.apiKey, SdkInternal.config.apiSecret, rt)
    }

    return result.fold(
      onSuccess = { info ->
        // 无论是业务方存储还是内存存储，统一调用接口
        p.saveToken(info.accessToken, info.refreshToken)
        info.accessToken
      },
      onFailure = {
        p.onTokenExpired()
        throw TokenRefreshException("token failed (get/refresh)", it)
      }
    )
  }
}

class TokenRefreshException(message: String, cause: Throwable? = null) : Exception(message, cause)