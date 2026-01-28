package com.sofar.kmp.network.internal

import com.sofar.kmp.network.core.TokenProvider

/**
 *  SDK 内部的内存存储实现，作为业务方未提供 Provider 时的兜底
 */
internal class MemoryTokenProvider : TokenProvider {
  private var accessToken: String? = null
  private var refreshToken: String? = null

  override fun getAccessToken(): String? = accessToken
  override fun getRefreshToken(): String? = refreshToken

  override fun saveToken(accessToken: String, refreshToken: String) {
    this.accessToken = accessToken
    this.refreshToken = refreshToken
  }

  override fun onTokenExpired() {
    accessToken = null
    refreshToken = null
  }
}