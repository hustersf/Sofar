package com.sofar.kmp.network.api

import com.sofar.kmp.network.api.model.TokenInfo
import com.sofar.kmp.network.internal.NetworkEngine
import com.sofar.kmp.network.internal.SdkHttp.safeRequest
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.http.HttpMethod

class AuthApi internal constructor(private val engine: NetworkEngine) {

  suspend fun getToken(
    clientId: String,
    clientSecret: String
  ): Result<TokenInfo> = engine.httpClient.safeRequest {
    method = HttpMethod.Post
    url("auth/token")
    parameter("client_id", clientId)
    parameter("client_secret", clientSecret)
  }

  suspend fun refreshToken(
    clientId: String,
    clientSecret: String,
    token: String?
  ): Result<TokenInfo> = engine.httpClient.safeRequest {
    method = HttpMethod.Post
    url("auth/refreshToken")
    parameter("client_id", clientId)
    parameter("client_secret", clientSecret)
    parameter("token", token)
  }
}