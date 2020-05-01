package com.sofar.network;


import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.sofar.network.exception.MyExceptionConsumer;
import com.sofar.network.gson.Gsons;
import com.sofar.network.retrofit.SofarRetrofitConfig;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;

public class MyRetrofitConfig extends SofarRetrofitConfig {

  public MyRetrofitConfig() {
    super("http://www.wanandroid.com/", Schedulers.newThread());
  }

  @NonNull
  @Override
  public Observable<?> buildObservable(Observable<?> input, Call<Object> call) {
    return super.buildObservable(input, call)
      .doOnNext(new MyExceptionConsumer());
  }

  @NonNull
  @Override
  public Gson buildGson() {
    return Gsons.STANDARD_GSON;
  }
}
