package com.sofar.network.exception;

import com.sofar.network.response.Response;

public class MyException extends Exception {

  public transient final Response<?> mResponse;

  public final int mCode;
  public final String mMessage;

  public MyException(Response<?> response) {
    mResponse = response;
    mCode = response.code();
    mMessage = response.message();
  }

  @Override
  public String getMessage() {
    return mMessage;
  }

  public int getErrorCode() {
    return mCode;
  }
}
