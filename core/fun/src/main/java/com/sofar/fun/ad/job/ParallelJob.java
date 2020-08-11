package com.sofar.fun.ad.job;

import androidx.annotation.NonNull;

import com.sofar.fun.ad.task.CountTask;
import com.sofar.fun.ad.task.ResultCallback;

import java.util.List;

/**
 * 并行执行任务
 */
public class ParallelJob<T> extends Job<T> {

  /**
   * 一次并行执行的任务数
   */
  int parallelCount = 1;

  ResultCallback<T> callback;

  int resultCount;

  public ParallelJob(@NonNull List<CountTask> tasks, int count) {
    super(tasks, count);
  }


  public void setParallelCount(int parallelCount) {
    this.parallelCount = Math.min(parallelCount, tasks.size());
  }

  @Override
  public void submit(ResultCallback<T> callback) {
    this.callback = callback;
    execute();
  }

  private void execute() {
    if (queue.isEmpty()) {
      if (callback != null) {
        callback.onResult(results);
      }
      return;
    }

    resultCount = 0;
    for (int i = 0; i < parallelCount; i++) {
      if (!queue.isEmpty()) {
        CountTask task = queue.poll();
        task.updateCount(count - results.size());
        executor.execute(task);
        task.postResult(list -> {
          results.addAll(list);
          resultCount++;
          checkResult();
        });
      } else {
        resultCount++;
      }
    }
  }

  private void checkResult() {
    //等待并发task都返回了
    if (resultCount == parallelCount) {
      if (results.size() >= count) {
        if (callback != null) {
          callback.onResult(results);
        }
      } else {
        execute();
      }
    }
  }
}
