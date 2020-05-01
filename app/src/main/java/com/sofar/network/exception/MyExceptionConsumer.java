package com.sofar.network.exception;


import com.sofar.network.response.Response;

import io.reactivex.functions.Consumer;

public class MyExceptionConsumer implements Consumer<Object> {

  @Override
  public void accept(Object o) throws Exception {
    if (o instanceof Response) {
      Response response = (Response) o;
      if (response.code() != 0) {
        throw new MyException(response);
      }
    }
  }
}
