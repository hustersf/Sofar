package com.sofar.network2.core

interface TokenProvider {
  fun getAccessToken(): String?
  fun getRefreshToken(): String?

  // 当静默刷新成功后，调用此方法通知业务方更新本地存储
  fun saveToken(accessToken: String, refreshToken: String)

  // 当刷新也失败时（彻底过期），通知业务方处理（如跳登录页）
  fun onTokenExpired()
}