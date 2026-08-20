package com.sofar.network.cache

import com.sofar.network.cache.model.RepoSearchResponse
import com.sofar.network.cache.retrofit.Cacheable
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubService {

  @Cacheable
  @GET("search/repositories?sort=stars")
  fun searchRepos(
    @Query("q") query: String,
    @Query("page") page: Int,
    @Query("per_page") itemsPerPage: Int
  ): Flow<RepoSearchResponse>
}