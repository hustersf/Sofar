package com.sofar.network2.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
  @SerialName("data") val data: T,
  @SerialName("errorCode") val errorCode: Int,
  @SerialName("errorMsg") val errorMsg: String = "",
)