package com.sofar.kmp.network.openapi.api

import com.sofar.kmp.network.openapi.api.model.ApiResponse
import com.sofar.kmp.network.openapi.api.model.Banner
import com.sofar.kmp.network.openapi.internal.ApiExecutor
import io.ktor.client.request.url
import io.ktor.http.HttpMethod

class BannerApi internal constructor(
  private val executor: ApiExecutor
) {

  suspend fun getBanners(): ApiResponse<List<Banner>> = executor.safeRequest {
    method = HttpMethod.Get
    url("/banner/json")
  }
}
