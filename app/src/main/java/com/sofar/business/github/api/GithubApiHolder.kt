package com.sofar.business.github.api

import com.sofar.network.ApiClient

object GithubApiHolder {

  private val apiClient: ApiClient by lazy {
    ApiClient(
      baseUrl = "https://api.github.com/"
    )
  }

  fun <T : Any> create(serviceClass: Class<T>): T {
    return apiClient.create(serviceClass)
  }

  val githubService: GithubService by lazy { create(GithubService::class.java) }
}