package com.sofar.network.response;

public class Response<T> {

  private final T mBody;
  private final int mCode;
  private final String mMessage;
  private okhttp3.Response mRawResponse;

  public Response(T body, int errorCode, String errorMessage) {
    mBody = body;
    mCode = errorCode;
    mMessage = errorMessage;
  }

  void setRawResponse(okhttp3.Response rawResponse) {
    mRawResponse = rawResponse;
  }

  public T body() {
    return mBody;
  }

  public okhttp3.Response raw() {
    return mRawResponse;
  }

  public int code() {
    return mCode;
  }

  public String message() {
    return mMessage;
  }

}
