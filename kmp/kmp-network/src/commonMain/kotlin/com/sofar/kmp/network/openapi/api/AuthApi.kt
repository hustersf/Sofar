package com.sofar.kmp.network.openapi.api

import com.sofar.kmp.network.openapi.api.model.ApiResponse
import com.sofar.kmp.network.openapi.api.model.TokenInfo
import com.sofar.kmp.network.openapi.internal.ApiExecutor
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.http.HttpMethod

class AuthApi internal constructor(private val executor: ApiExecutor) {

  suspend fun getToken(
    clientId: String,
    clientSecret: String
  ): ApiResponse<TokenInfo> = executor.safeRequest {
    method = HttpMethod.Post
    url("auth/token")
    parameter("client_id", clientId)
    parameter("client_secret", clientSecret)
  }

  suspend fun refreshToken(
    clientId: String,
    clientSecret: String,
    token: String?
  ): ApiResponse<TokenInfo> = executor.safeRequest {
    method = HttpMethod.Post
    url("auth/refreshToken")
    parameter("client_id", clientId)
    parameter("client_secret", clientSecret)
    parameter("token", token)
  }
}