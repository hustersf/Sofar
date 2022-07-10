package com.sofar.ad.task;

import androidx.annotation.NonNull;

import com.sofar.ad.job.ParallelDelayJob;

import java.util.ArrayList;
import java.util.List;

public class ParallelDelayCountTask<T> extends CountTask {

  List<Task<T>> subTasks = new ArrayList<>();
  long delay = 0;

  @Override
  public void onExecute() {
    ParallelDelayJob job = new ParallelDelayJob(subTasks, count);
    job.setDelay(delay);
    job.submit(results -> {
      postResult(results);
    });
  }

  /**
   * 设置任务之间的执行间隔时间
   *
   * @param delay 单位ms
   */
  public void setDelay(long delay) {
    this.delay = delay;
  }

  public void addTask(@NonNull Task task) {
    subTasks.add(task);
  }

}
