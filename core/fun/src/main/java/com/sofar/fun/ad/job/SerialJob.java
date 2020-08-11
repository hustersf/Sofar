package com.sofar.fun.ad.job;

import androidx.annotation.NonNull;

import com.sofar.fun.ad.task.CountTask;
import com.sofar.fun.ad.task.ResultCallback;

import java.util.List;

/**
 * 串行执行任务
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
      task.postResult(list -> {
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
