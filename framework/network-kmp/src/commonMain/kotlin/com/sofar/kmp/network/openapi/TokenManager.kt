package com.sofar.kmp.network.openapi

import io.ktor.client.statement.HttpResponse

/**
 * 身份验证管理器接口
 */
interface TokenManager {
  fun isExpired(response: HttpResponse, errorCode: Int?): Boolean
  fun getCurrentToken(): String?
  suspend fun refreshAndGet(oldToken: String?): String?
}

/** 凭证标记接口 */
interface AuthData

/** 业务方实现的获取逻辑 */
fun interface TokenFetcher {
  suspend fun fetch(): AuthData?
}
