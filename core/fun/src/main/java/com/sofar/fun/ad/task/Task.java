package com.sofar.fun.ad.task;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;

public abstract class Task<T> implements Runnable {

  ResultCallback<T> callback;

  @Override
  public void run() {
    onExecute();
  }

  public abstract void onExecute();

  public void result(@NonNull List<T> results) {
    if (callback != null) {
      callback.onResult(results);
    }
  }

  public void error() {
    if (callback != null) {
      callback.onResult(Collections.emptyList());
    }
  }

  public void postResult(ResultCallback<T> callback) {
    this.callback = callback;
  }

}
