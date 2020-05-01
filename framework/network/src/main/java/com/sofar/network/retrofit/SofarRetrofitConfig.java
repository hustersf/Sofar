package com.sofar.network.retrofit;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.sofar.network.cookie.SimpleCookieJar;
import com.sofar.network.interceptor.ContentLengthInterceptor;
import com.sofar.network.interceptor.HeadersInterceptor;
import com.sofar.network.interceptor.ParamsInterceptor;
import com.sofar.network.retrofit.consumer.NetworkCounter;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.OkHttpClient;
import retrofit2.Call;

public class SofarRetrofitConfig implements RetrofitConfig {

  protected static final int DEFAULT_TIME_OUT = 30;// 超时时间 30s

  String baseUrl;
  Scheduler scheduler;

  static {
    RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
      @Override
      public void accept(Throwable throwable) throws Exception {
        // eat all rx exception.
      }
    });
  }

  public SofarRetrofitConfig(@NonNull String baseUrl, @NonNull Scheduler scheduler) {
    this.baseUrl = baseUrl;
    this.scheduler = scheduler;
  }

  @NonNull
  @Override
  public String buildBaseUrl() {
    return baseUrl;
  }

  @NonNull
  @Override
  public Params buildParams() {
    return new SofarParams();
  }

  @NonNull
  @Override
  public OkHttpClient buildClient() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);// 连接超时时间
    builder.writeTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);// 写操作 超时时间
    builder.readTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);// 读操作超时时间

    Params params = buildParams();
    builder.addInterceptor(new HeadersInterceptor(params));
    builder.addInterceptor(new ParamsInterceptor(params));
    builder.addInterceptor(new ContentLengthInterceptor());

    builder.cookieJar(new SimpleCookieJar());
    return builder.build();
  }

  @NonNull
  @Override
  public Gson buildGson() {
    return new Gson();
  }

  @NonNull
  @Override
  public Scheduler buildExecuteScheduler() {
    return scheduler;
  }

  @NonNull
  @Override
  public Call<Object> buildCall(Call<Object> call) {
    return call;
  }

  @NonNull
  @Override
  public Observable<?> buildObservable(Observable<?> input, Call<Object> call) {
    return input.observeOn(AndroidSchedulers.mainThread())
      .doOnComplete(NetworkCounter.ON_COMPLETE)
      .doOnError(NetworkCounter.ON_ERROR);
  }
}
