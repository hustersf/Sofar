package com.sofar.business.github.api;

import com.sofar.business.github.model.RepoSearchResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GithubService {

  @GET("search/repositories?sort=stars")
  Observable<RepoSearchResponse> searchRepos(@Query("q") String query,
                                             @Query("page") int page,
                                             @Query("per_page") int itemsPerPage);

}
