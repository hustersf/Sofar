package com.sofar.network2.api

class ApiException(
  val errorCode: Int,
  val errorMsg: String,
) : RuntimeException("API Business Failure[$errorCode]: $errorMsg") {

  // 增加一些快捷判断，方便业务方使用
  fun isTokenExpired(): Boolean = errorCode == 401
  fun isParamError(): Boolean = errorCode == 400
}

fun <T> Result<ApiResponse<T>>.unwrap(): Result<T> {
  return this.mapCatching { response ->
    if (response.errorCode == 0) {
      response.data
    } else {
      // 这里 throw 会被 Result.map 捕获，从而转为 Result.failure
      throw ApiException(response.errorCode, response.errorMsg)
    }
  }
}

suspend inline fun <S, T> S.execute(
  crossinline call: suspend S.() -> Result<ApiResponse<T>>,
): Result<T> {
  return this.call().unwrap()
}