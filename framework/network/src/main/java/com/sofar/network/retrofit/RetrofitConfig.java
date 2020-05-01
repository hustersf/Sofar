package com.sofar.network.retrofit;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import okhttp3.OkHttpClient;
import retrofit2.Call;

public interface RetrofitConfig {

  @NonNull
  String buildBaseUrl();

  @NonNull
  Params buildParams();

  @NonNull
  OkHttpClient buildClient();

  @NonNull
  Gson buildGson();

  @NonNull
  Scheduler buildExecuteScheduler();

  @NonNull
  Call<Object> buildCall(Call<Object> call);

  @NonNull
  Observable<?> buildObservable(Observable<?> o, Call<Object> call);

  interface Params {

    @NonNull
    Map<String, String> getHeaderParams();

    @NonNull
    Map<String, String> getUrlParams();

    @NonNull
    Map<String, String> getBodyParams();
  }

}
