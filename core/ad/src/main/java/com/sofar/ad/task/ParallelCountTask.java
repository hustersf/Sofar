package com.sofar.ad.task;

import androidx.annotation.NonNull;

import com.sofar.ad.job.ParallelJob;

import java.util.ArrayList;
import java.util.List;

public class ParallelCountTask<T> extends CountTask {

  List<Task<T>> subTasks = new ArrayList<>();

  @Override
  public void onExecute() {
    ParallelJob job = new ParallelJob(subTasks, count);
    job.submit(results -> {
      postResult(results);
    });
  }

  public void addTask(@NonNull Task task) {
    subTasks.add(task);
  }

}
