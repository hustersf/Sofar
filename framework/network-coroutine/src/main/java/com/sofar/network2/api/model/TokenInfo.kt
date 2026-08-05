package com.sofar.network2.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenInfo(
  @SerialName("accessToken") val accessToken: String,
  @SerialName("refreshToken") val refreshToken: String,
  @SerialName("expiresIn") val expiresIn: Int,
  @SerialName("tokenType") val tokenType: String,
)