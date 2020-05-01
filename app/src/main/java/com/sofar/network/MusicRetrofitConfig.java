package com.sofar.network;


import com.sofar.network.retrofit.SofarRetrofitConfig;

import io.reactivex.schedulers.Schedulers;

public class MusicRetrofitConfig extends SofarRetrofitConfig {

  public MusicRetrofitConfig() {
    super("http://musicapi.qianqian.com/", Schedulers.newThread());
  }
}
