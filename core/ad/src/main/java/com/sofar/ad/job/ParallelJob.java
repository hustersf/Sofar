package com.sofar.ad.job;

import androidx.annotation.NonNull;

import com.sofar.ad.task.CountTask;
import com.sofar.ad.task.ResultCallback;
import com.sofar.ad.task.Task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 并行执行任务
 * 假设有一组任务   t1,t2,t3
 * t1,t2,t3会同时执行
 * <p>
 * 默认任务的优先级关系是t1>t2>t3
 * 优先级低的任务有结果返回时，还需要等待优先级高的任务的结果
 * 假如预期需要3条数据，t1,t2,t3均返回2条
 * 则会从t1中拿2条，t2中拿1条，并将结果返回，剩余的将会被抛弃
 */
public class ParallelJob<T> extends Job<T> {

  ResultCallback<T> callback;

  /**
   * 任务返回结果
   */
  LinkedHashMap<Task, List<T>> resultMap = new LinkedHashMap<>();

  public ParallelJob(@NonNull List<CountTask> tasks, int count) {
    super(tasks, count);
  }


  @Override
  public void submit(ResultCallback<T> callback) {
    this.callback = callback;
    execute();
  }

  private void execute() {
    for (int i = 0; i < tasks.size(); i++) {
      CountTask task = tasks.get(i);
      task.updateCount(count);
      executeTask(task);
      resultMap.put(task, null);
    }
  }

  private void executeTask(@NonNull Task task) {
    task.awaitResult(results -> {
      synchronized (this) {
        resultMap.put(task, results);
        if (canceled) {
          task.abandon(results);
        } else {
          checkResult();
        }
      }
    });
    executor.execute(task);
  }

  private void checkResult() {
    int resultCount = 0;
    boolean resultFull = true;
    for (Map.Entry<Task, List<T>> item : resultMap.entrySet()) {
      List<T> r = item.getValue();
      if (r == null) {
        resultFull = false;
        break;
      } else {
        resultCount += r.size();
      }
    }

    if (resultFull || resultCount >= count) {
      sendResult();
    }
  }


  private void sendResult() {
    results.clear();
    for (Map.Entry<Task, List<T>> item : resultMap.entrySet()) {
      Task task = item.getKey();
      List<T> list = item.getValue();
      if (list == null) {
        return;
      }

      List<T> abandonList = new ArrayList<>(list);
      if (results.size() < count) {
        for (T t : list) {
          if (results.size() < count) {
            results.add(t);
            abandonList.remove(t);
          } else {
            break;
          }
        }

        if (results.size() >= count && callback != null) {
          callback.onResult(results);
        }
      }

      if (abandonList.size() > 0) {
        task.abandon(abandonList);
      }
    }

    if (results.size() < count && callback != null) {
      callback.onResult(results);
    }
    cancel();
  }

  @Override
  public void cancel() {
    super.cancel();
    resultMap.clear();
  }
}
