package com.sofar.ad.job;

import androidx.annotation.NonNull;

import com.sofar.ad.task.CountTask;
import com.sofar.ad.task.ResultCallback;
import com.sofar.ad.task.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * n个task，预期得到count条结果返回
 */
public abstract class Job<T> {

  @NonNull
  protected List<CountTask> tasks;
  protected int count;
  protected Queue<CountTask> queue = new LinkedList<>();

  protected boolean canceled;

  protected static ExecutorService executor =
    Executors.newCachedThreadPool(new NameThreadFactory("Job"));

  @NonNull
  protected List<T> results;

  public Job(@NonNull List<CountTask> tasks, int count) {
    this.tasks = tasks;
    this.count = count;
    queue.addAll(tasks);
    results = new ArrayList<>(count);
  }

  public abstract void submit(ResultCallback<T> callback);

  public void cancel() {
    for (Task task : tasks) {
      task.cancel();
    }
    canceled = true;
  }

}
