package com.sofar.business.github.api

import com.sofar.business.github.model.RepoSearchResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubService {
  @GET("search/repositories?sort=stars")
  suspend fun searchRepos(
    @Query("q") query: String,
    @Query("page") page: Int,
    @Query("per_page") itemsPerPage: Int
  ): RepoSearchResponse

  @GET("search/repositories?sort=stars")
  fun searchReposStr(
    @Query("q") query: String,
    @Query("page") page: Int,
    @Query("per_page") itemsPerPage: Int
  ): Observable<String>
}
