package com.sofar.ad.job;

import androidx.annotation.NonNull;

import com.sofar.ad.task.CountTask;
import com.sofar.ad.task.ResultCallback;

import java.util.List;

/**
 * 串行执行任务
 * <p>
 * 假设有3个task  t1,t2,t3
 * 如果t1执行结果数量满足要求，则直接返回结果
 * 反之，会继续执行t2，直到数量满足要求或者任务全部执行结束
 */
public class SerialJob<T> extends Job<T> {

  ResultCallback<T> callback;

  public SerialJob(@NonNull List<CountTask> tasks, int count) {
    super(tasks, count);
  }

  @Override
  public void submit(ResultCallback<T> callback) {
    this.callback = callback;
    execute();
  }


  private void execute() {
    if (!queue.isEmpty()) {
      CountTask task = queue.poll();
      task.updateCount(count - results.size());
      executor.execute(task);
      task.awaitResult(list -> {
        results.addAll(list);
        checkResult();
      });
    } else {
      if (callback != null) {
        callback.onResult(results);
      }
    }
  }

  private void checkResult() {
    if (results.size() >= count) {
      if (callback != null) {
        callback.onResult(results);
      }
    } else {
      execute();
    }
  }

}
