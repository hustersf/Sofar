package com.sofar.kmp.network.openapi.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
  @SerialName("data") val data: T? = null,
  @SerialName("errorCode") val errorCode: Int,
  @SerialName("errorMsg") val errorMsg: String = "",
) {
  val isSuccess: Boolean
    get() = errorCode == 0

  companion object {
    const val NETWORK_ERROR = -1            // 兜底错误
    const val ERROR_CONNECT_FAILED = -2     // 连接失败
    const val ERROR_TIMEOUT = -3            // 请求超时
    const val ERROR_PARSING_FAILED = -4     // 反序列化解析失败
  }
}