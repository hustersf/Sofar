package com.sofar.network.openapi.api

import com.sofar.network.openapi.api.model.Banner
import retrofit2.http.GET

interface ApiService {

  @GET("/banner/json")
  suspend fun getBannerDataResponse(): Result<String>

  @GET("/banner/json")
  suspend fun getBannerData(): Result<ApiResponse<List<Banner>>>
}
