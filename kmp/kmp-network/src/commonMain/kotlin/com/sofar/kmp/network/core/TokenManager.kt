package com.sofar.kmp.network.core

import io.ktor.client.statement.HttpResponse

/** 凭证标记接口 */
interface AuthData

/** 外部模式凭证 */
class OpenAuthData(val accessToken: String) : AuthData

/** 业务方实现的获取逻辑 */
fun interface TokenFetcher {
  suspend fun fetch(): AuthData?
}

/** 内部统一的管理器接口 */
internal interface TokenManager {
  fun isExpired(response: HttpResponse, errorCode: Int?): Boolean
  fun getCurrentToken(): String?
  suspend fun refreshAndGet(oldToken: String?): String?
}

/** 全局当前的管理器引用（由门面类在 init 时注入） */
internal var currentTokenManager: TokenManager? = null
