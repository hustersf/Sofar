package com.sofar.business.github.api;


import com.sofar.network.retrofit.SofarRetrofitConfig;

import io.reactivex.schedulers.Schedulers;


public class GithubRetrofitConfig extends SofarRetrofitConfig {

  public GithubRetrofitConfig() {
    super("https://api.github.com/", Schedulers.newThread());
  }
}
