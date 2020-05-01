package com.sofar.network;

import com.sofar.network.response.Response;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface ApiService {

  @GET("/banner/json")
  Observable<String> getBannerData();

  @GET("/banner/json")
  Observable<Response<String>> getBannerDataResponse();

}
