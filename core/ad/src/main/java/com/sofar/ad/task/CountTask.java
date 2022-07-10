package com.sofar.ad.task;

/**
 * 当前task,预期返回count条结果
 */
public abstract class CountTask<T> extends Task<T> {

  protected int count;

  public void updateCount(int count) {
    this.count = count;
  }

}
