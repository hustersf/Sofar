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
   * 因某种原因 此次结果被丢弃
   * 如需要2条数据，但是返回了3条数据，则多余的1条数据将被丢弃
   *
   * @param list 此次任务被丢弃的结果
   */
  public void abandon(@NonNull List<T> list) {

  }

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
