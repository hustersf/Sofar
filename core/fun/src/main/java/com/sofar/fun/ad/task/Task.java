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

  /**
   * 任务执行的具体业务逻辑
   */
  public abstract void onExecute();

  /**
   * 任务被取消执行
   */
  public void cancel() {
  }

  /**
   * 任务执行成功时，调用此方法
   *
   * @param results
   */
  public void postResult(@NonNull List<T> results) {
    if (callback != null) {
      callback.onResult(results);
    }
  }

  /**
   * 任务执行发生错误时，调用此方法
   */
  public void postError() {
    if (callback != null) {
      callback.onResult(Collections.emptyList());
    }
  }

  /**
   * 任务执行策略，依赖每一个任务的执行结果
   *
   * @param callback
   */
  public void awaitResult(ResultCallback<T> callback) {
    this.callback = callback;
  }

}
