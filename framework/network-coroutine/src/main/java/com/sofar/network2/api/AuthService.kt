package com.sofar.network2.api

import com.sofar.network2.api.model.TokenInfo
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {

  @POST("auth/token")
  suspend fun getToken(
    @Query("client_id") clientId: String,
    @Query("client_secret") clientSecret: String,
  ): Result<ApiResponse<TokenInfo>>

  @POST("auth/refreshToken")
  suspend fun refreshToken(
    @Query("client_id") clientId: String,
    @Query("client_secret") clientSecret: String,
    @Query("token") token: String?,
  ): Result<ApiResponse<TokenInfo>>
}