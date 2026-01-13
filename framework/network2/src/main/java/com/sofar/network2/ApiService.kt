package com.sofar.network2

import com.sofar.network2.api.ApiResponse
import com.sofar.network2.api.model.Banner
import retrofit2.http.GET

interface ApiService {

  @GET("/banner/json")
  suspend fun getBannerDataResponse(): Result<String>

  @GET("/banner/json")
  suspend fun getBannerData(): Result<ApiResponse<List<Banner>>>
}
