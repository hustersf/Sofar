package com.sofar.startup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;

import com.sofar.startup.executor.TaskExecutor;
import com.sofar.startup.graph.TaskGraph;
import com.sofar.startup.task.DependencyTask;

public class StartUpDispatcher {

  private List<DependencyTask> mStartUpTasks;
  private List<DependencyTask> mMainThreadTasks;
  private List<DependencyTask> mThreadPoolTasks;

  //存储每个task的子task
  private HashMap<Class<? extends DependencyTask>, ArrayList<DependencyTask>> mChildTaskMap =
    new HashMap<>();

  private static class Inner {
    private static StartUpDispatcher INSTANCE = new StartUpDispatcher();
  }

  private StartUpDispatcher() {
    mStartUpTasks = new ArrayList<>();
    mMainThreadTasks = new ArrayList<>();
    mThreadPoolTasks = new ArrayList<>();
  }

  public static StartUpDispatcher get() {
    return Inner.INSTANCE;
  }

  public StartUpDispatcher addTask(@NonNull DependencyTask task) {
    mStartUpTasks.add(task);
    return this;
  }

  public StartUpDispatcher addTasks(@NonNull List<DependencyTask> tasks) {
    mStartUpTasks.addAll(tasks);
    return this;
  }

  public void start() {
    mChildTaskMap.clear();
    List<DependencyTask> sortList = TaskGraph.getSortTasks(mStartUpTasks, mChildTaskMap);
    mMainThreadTasks.clear();
    mThreadPoolTasks.clear();
    for (DependencyTask task : sortList) {
      if (task.runOnMainThread()) {
        mMainThreadTasks.add(task);
      } else {
        mThreadPoolTasks.add(task);
      }
    }
    dispatchTasks();
  }

  private void dispatchTasks() {
    //子线程的调度放前面
    for (DependencyTask task : mThreadPoolTasks) {
      TaskExecutor.runExecutor.execute(task);
    }
    for (DependencyTask task : mMainThreadTasks) {
      task.run();
    }
  }

  /**
   * 某任务执行结束，需要调用此方法 唤醒子任务
   */
  public void taskFinish(DependencyTask task) {
    for (DependencyTask childTask : mChildTaskMap.get(task.getClass())) {
      childTask.taskNotify();
    }
  }
}
