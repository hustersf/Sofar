package com.sofar.startup.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import androidx.annotation.NonNull;

import com.sofar.startup.task.DependencyTask;

public class TaskGraph {

  public static List<DependencyTask> getSortTasks(@NonNull List<DependencyTask> startUpTasks,
    @NonNull HashMap<Class<? extends DependencyTask>, ArrayList<DependencyTask>> childTaskMap) {
    List<DependencyTask> sortTasks = new ArrayList<>();
    //入度为0才能入队
    Queue<DependencyTask> queue = new LinkedList<>();
    //存储每个task的入度
    HashMap<DependencyTask, Integer> inDegreeMap = new HashMap<>();

    for (DependencyTask task : startUpTasks) {
      int inDegree = task.getDependTasks() == null ? 0 : task.getDependTasks().size();
      inDegreeMap.put(task, inDegree);
      if (inDegree == 0) {
        queue.add(task);
      }
      if (inDegree > 0) {
        //这里的 dependCls 是当前 task 的父任务
        for (Class<? extends DependencyTask> dependCls : task.getDependTasks()) {
          if (!childTaskMap.containsKey(dependCls)) {
            childTaskMap.put(dependCls, new ArrayList());
          }
          childTaskMap.get(dependCls).add(task);
        }
      }
    }

    while (!queue.isEmpty()) {
      DependencyTask outTask = queue.poll();
      sortTasks.add(outTask);
      //出队任务的 子任务入度减1
      for (DependencyTask childTask : childTaskMap.get(outTask.getClass())) {
        int inDegree = inDegreeMap.get(childTask) - 1;
        inDegreeMap.put(childTask, inDegree);
        if (inDegree == 0) {
          queue.add(childTask);
        }
      }
    }

    if (sortTasks.size() != startUpTasks.size()) {
      throw new IllegalStateException("task graph 存在环");
    }
    return sortTasks;
  }
}
