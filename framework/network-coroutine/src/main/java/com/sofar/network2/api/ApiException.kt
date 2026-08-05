package com.sofar.network2.api

class ApiException(
  val errorCode: Int,
  val errorMsg: String,
) : RuntimeException("API Business Failure[$errorCode]: $errorMsg") {

  companion object {
    /** 服务器返回成功但 data 字段缺失 */
    const val CODE_EMPTY_DATA = -1
  }

  // 增加一些快捷判断，方便业务方使用
}

inline fun <reified T> Result<ApiResponse<T>>.unwrap(): Result<T> {
  return this.mapCatching { response ->
    if (response.errorCode == 0) {
      val data = response.data
      when {
        data != null -> data
        Unit is T -> Unit as T
        else -> throw ApiException(
          ApiException.CODE_EMPTY_DATA,
          "Response data is null, but expected ${T::class.simpleName}"
        )
      }
    } else {
      // 这里 throw 会被 Result.map 捕获，从而转为 Result.failure
      throw ApiException(response.errorCode, response.errorMsg)
    }
  }
}

suspend inline fun <S, reified T> S.execute(
  crossinline call: suspend S.() -> Result<ApiResponse<T>>,
): Result<T> {
  return this.call().unwrap()
}