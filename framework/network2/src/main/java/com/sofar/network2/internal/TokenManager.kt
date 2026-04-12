package com.sofar.network2.internal

import com.sofar.network2.api.AuthService
import com.sofar.network2.api.unwrap
import com.sofar.network2.core.SdkConfig
import com.sofar.network2.core.TokenProvider
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class TokenManager(
  private val config: SdkConfig,
  private val authServiceApi: () -> AuthService
) {
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
    val authService = authServiceApi()

    val result = if (rt.isNullOrEmpty()) {
      // 策略 A: 初始登录
      authService.getToken(config.apiKey, config.apiSecret).unwrap()
    } else {
      // 策略 B: 刷新令牌
      authService.refreshToken(config.apiKey, config.apiSecret, rt).unwrap()
    }

    return result.fold(
      onSuccess = { info ->
        // 无论是业务方存储还是内存存储，统一调用接口
        p.saveToken(info.accessToken, info.refreshToken)
        info.accessToken
      },
      onFailure = {
        p.onTokenExpired()
        null
      }
    )
  }
}