package com.sofar.network.response2;


import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * 取出 {@link Response 中的data数据}
 */
public class ResponseFunction<T> implements Function<Response<T>, T> {

  @Override
  public T apply(@NonNull Response<T> response) throws Exception {
    return response.data;
  }
}
