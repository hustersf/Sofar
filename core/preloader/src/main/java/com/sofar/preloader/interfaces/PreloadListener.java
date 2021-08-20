package com.sofar.preloader.interfaces;

public interface PreloadListener<T> {

  void onResponse(T response);

  default void onError(Throwable e) {}

}
