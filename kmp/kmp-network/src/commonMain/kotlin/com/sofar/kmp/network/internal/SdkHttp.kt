package com.sofar.kmp.network.internal

import com.sofar.kmp.network.api.model.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApiException(
  val code: Int,
  msg: String?
) : Exception("API Error($code): $msg")

class TokenException(
  val code: Int,
  message: String
) : Exception("[$code] $message")

internal object SdkHttp {
  const val CODE_EMPTY_DATA = -1
  val TOKEN_INVALID_CODES = setOf(-44112)

  suspend inline fun <reified T> HttpResponse.safeParse(): ApiResponse<T> {
    val rawText = bodyAsText()
    return NetworkEngine.sdkJson.decodeFromString(rawText)
  }

  suspend inline fun <reified T> HttpClient.safeRequest(
    crossinline block: HttpRequestBuilder.() -> Unit
  ): Result<T> = withContext(Dispatchers.Default) {
    try {
      // 尝试第一次请求
      val response = request { block() }
      val apiResponse = response.safeParse<T>()
      Result.success(apiResponse.unwrap())
    } catch (e: TokenException) {
      // 捕获 TokenException（来自 Validator 的 401 或 unwrap 的业务码）
      runCatching {
        TokenManager.refreshAndGet(TokenManager.getAccessToken())
        // 刷新成功后发起第二次请求
        val retryResponse = request { block() }
        retryResponse.safeParse<T>().unwrap()
      }
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

  inline fun <reified T> ApiResponse<T>.unwrap(): T {
    if (errorCode == 0) {
      val data = this.data
      return when {
        data != null -> data
        Unit is T -> Unit as T
        else -> throw ApiException(
          CODE_EMPTY_DATA,
          "Response data is null, but expected ${T::class.simpleName}"
        )
      }
    }

    // 如果是 Token 错误码，抛出 TokenException 触发后续的 recover
    if (TOKEN_INVALID_CODES.contains(errorCode)) {
      throw TokenException(errorCode, errorMsg)
    }

    throw ApiException(errorCode, errorMsg)
  }
}