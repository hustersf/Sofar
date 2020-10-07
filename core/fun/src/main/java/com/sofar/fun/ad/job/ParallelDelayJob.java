package com.sofar.fun.ad.job;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.sofar.fun.ad.task.CountTask;
import com.sofar.fun.ad.task.ResultCallback;
import com.sofar.fun.ad.task.Task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 并行执行一组任务，任务之间可以设置延迟执行
 * 如果延迟时间为0，则这组任务将会同时执行
 * <p>
 * 假设有一组任务   t1,t2,t3    延迟执行时间为1s
 * 执行任务t1，t1在1s内有结果返回，结果条数满足要求，则直接返回结果,否则立马执行t2
 * 如果t1在1s后仍无结果返回，会继续执行任务t2
 * t2在1s内仍无结果返回，会继续执行任务t3
 * <p>
 * 默认任务的优先级关系是t1>t2>t3
 * 优先级低的任务有结果返回时，还需要等待优先级高的任务的结果
 */
public class ParallelDelayJob<T> extends Job<T> {

  ResultCallback<T> callback;

  Handler handler = new Handler(Looper.getMainLooper());
  long delay = 0;

  /**
   * 任务返回结果
   */
  LinkedHashMap<Task, List<T>> resultMap = new LinkedHashMap<>();

  /**
   * 任务执行的runnable
   */
  LinkedHashMap<Task, Runnable> runMap = new LinkedHashMap<>();
  LinkedHashMap<Task, Boolean> taskStartMap = new LinkedHashMap<>();

  public ParallelDelayJob(@NonNull List<CountTask> tasks, int count) {
    super(tasks, count);
  }

  /**
   * 设置任务之间的执行间隔时间
   *
   * @param delay 单位ms
   */
  public void setDelay(long delay) {
    this.delay = delay;
  }

  @Override
  public void submit(ResultCallback<T> callback) {
    this.callback = callback;
    execute();
  }

  private void execute() {
    for (int i = 0; i < tasks.size(); i++) {
      Task task = tasks.get(i);
      Runnable runnable = () -> executeTask(task);
      runMap.put(task, runnable);
      resultMap.put(task, null);
      //延迟执行任务
      handler.postDelayed(runnable, i * delay);
    }
  }

  private void executeTask(@NonNull Task task) {
    if (taskStartMap.get(task) != null && taskStartMap.get(task)) {
      return;
    }

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
    taskStartMap.put(task, true);
  }

  private void checkResult() {
    int resultCount = 0;
    boolean resultFull = true;
    for (Map.Entry<Task, List<T>> item : resultMap.entrySet()) {
      Task task = item.getKey();
      List<T> r = item.getValue();
      if (r == null) {
        resultFull = false;
        break;
      } else {
        resultCount += r.size();
        int index = tasks.indexOf(task);
        if (index >= 0 && index < tasks.size() - 1) {
          Task nextTask = tasks.get(index + 1);
          handler.removeCallbacks(runMap.get(nextTask));
          if (resultCount < count) {
            executeTask(nextTask);
          }
        }
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
    runMap.clear();
    taskStartMap.clear();
  }
}
