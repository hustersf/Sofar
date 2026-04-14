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
}