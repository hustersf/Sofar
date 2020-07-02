package com.sofar.network;

import com.sofar.business.github.api.GithubRetrofitConfig;
import com.sofar.business.github.api.GithubService;
import com.sofar.network.retrofit.RetrofitFactory;

public class ApiProvider {

  private static class ApiServiceHolderClass {
    private final static ApiService INSTANCE = RetrofitFactory.newBuilder(new MyRetrofitConfig()).build().create(ApiService.class);
  }

  private static class MusicApiServiceHolderClass {
    private final static MusicApiService INSTANCE = RetrofitFactory.newBuilder(new MusicRetrofitConfig()).build().create(MusicApiService.class);
  }


  public static ApiService getApiService() {
    return ApiServiceHolderClass.INSTANCE;
  }

  public static MusicApiService getMusicApiService() {
    return MusicApiServiceHolderClass.INSTANCE;
  }


  private static class GithubServiceHolderClass {
    private final static GithubService INSTANCE = RetrofitFactory.newBuilder(new GithubRetrofitConfig()).build().create(GithubService.class);
  }

  public static GithubService getGithubService() {
    return GithubServiceHolderClass.INSTANCE;
  }
}
