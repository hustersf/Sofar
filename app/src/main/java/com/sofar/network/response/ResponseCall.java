package com.sofar.network.response;

import java.io.IOException;

import okhttp3.Request;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResponseCall<T> implements Call<T> {

  public final Call<T> mRawCall;

  public ResponseCall(Call<T> rawCall) {
    mRawCall = rawCall;
  }

  @Override
  @SuppressWarnings("unchecked")
  public retrofit2.Response<T> execute() throws IOException {
    retrofit2.Response<T> response = mRawCall.execute();
    T body = response.body();
    if (body instanceof com.sofar.network.response.Response) {
      ((com.sofar.network.response.Response) body).setRawResponse(response.raw());
    }
    return response;
  }

  @Override
  public void enqueue(final Callback<T> callback) {
    mRawCall.enqueue(new Callback<T>() {
      @Override
      @SuppressWarnings("unchecked")
      public void onResponse(Call<T> call, Response<T> response) {
        T body = response.body();
        if (body instanceof com.sofar.network.response.Response) {
          ((com.sofar.network.response.Response) body).setRawResponse(response.raw());
        }
        callback.onResponse(call, response);
      }

      @Override
      public void onFailure(Call<T> call, Throwable t) {
        callback.onFailure(call, t);
      }
    });
  }

  @Override
  public boolean isExecuted() {
    return mRawCall.isExecuted();
  }

  @Override
  public void cancel() {
    mRawCall.cancel();
  }

  @Override
  public boolean isCanceled() {
    return mRawCall.isCanceled();
  }

  @Override
  @SuppressWarnings("CloneDoesntCallSuperClone")
  public Call<T> clone() {
    return new ResponseCall<>(mRawCall.clone());
  }

  @Override
  public Request request() {
    return mRawCall.request();
  }

  @Override
  public Timeout timeout() {
    return null;
  }
}
