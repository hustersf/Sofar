package com.sofar.kmp.network.api

import com.sofar.kmp.network.api.model.Banner
import com.sofar.kmp.network.internal.NetworkEngine
import com.sofar.kmp.network.internal.SdkHttp.safeRequest
import io.ktor.client.request.url
import io.ktor.http.HttpMethod

class BannerApi internal constructor(
  private val engine: NetworkEngine
) {

  suspend fun getBanners(): Result<List<Banner>> = engine.httpClient.safeRequest {
    method = HttpMethod.Get
    url("/banner/json")
  }
}