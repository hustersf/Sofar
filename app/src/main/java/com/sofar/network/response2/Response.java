package com.sofar.network.response2;

import com.google.gson.annotations.SerializedName;

/**
 * 替代 {@link com.sofar.network.response.Response}
 */
public class Response<T> {

  @SerializedName("errorCode")
  int errorCode;

  @SerializedName("errorMsg")
  String errorMsg;

  @SerializedName("data")
  T data;
}
