package com.sofar.network2.internal

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 兼容标准 Http 401 和 业务 未鉴权
 */
class TokenRetryInterceptor(private val json: Json) : Interceptor {

  // 影子类：仅用于在拦截器中精确解析 errorCode，避免解析整个巨大的 data 节点
  @Serializable
  private data class ErrorCodeDetector(val errorCode: Int)

  private val refreshRequiredCodes = setOf(401, 402, 403, 1005)

  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()
    val response = chain.proceed(request)

    var shouldRefresh = response.code == 401
    if (!shouldRefresh) {
      // 1KB 足够装下包含 errorCode 的 JSON 外壳。
      val bodyString = response.peekBody(1024).string()

      // 精确解析 errorCode
      val errorCode = try {
        json.decodeFromString<ErrorCodeDetector>(bodyString).errorCode
      } catch (e: Exception) {
        -1
      }
      if (errorCode in refreshRequiredCodes) {
        shouldRefresh = true
      }
    }

    if (shouldRefresh) {
      // 获取请求中携带的旧 Token（用于给 TokenManager 做二次检查防止并发重复刷新）
      val oldToken = response.request.header("Authorization")?.removePrefix("AccessToken=")

      // 同步执行刷新逻辑。
      val newToken = runBlocking {
        TokenManager.refreshAndGet(oldToken)
      }

      if (newToken != null) {
        //  刷新成功：必须先关闭当前的旧响应流
        response.close()

        // 发起静默重试，让请求重新流经后续的所有拦截器
        val newRequest = request.newBuilder()
          .build()
        return chain.proceed(newRequest)
      }
    }

    return response
  }
}