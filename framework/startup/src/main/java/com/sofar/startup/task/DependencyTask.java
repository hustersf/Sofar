package com.sofar.startup.task;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.sofar.startup.StartUpDispatcher;

public abstract class DependencyTask implements Runnable {

  private CountDownLatch depends =
    new CountDownLatch(getDependTasks() == null ? 0 : getDependTasks().size());

  /**
   * 运行在主线程或子线程
   */
  @Override
  public void run() {
    //等待父任务执行完毕
    taskWait();
    //在这里可以统计任务的执行时间
    execute();
    StartUpDispatcher.get().taskFinish(this);
  }

  //执行任务
  protected abstract void execute();

  public void taskWait() {
    try {
      depends.wait();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * 依赖父任务调用
   */
  public void taskNotify() {
    depends.countDown();
  }

  /**
   * 自己依赖的task
   */
  public List<Class<? extends DependencyTask>> getDependTasks() {
    return null;
  }

  /**
   * 是否运行在主线程
   */
  public boolean runOnMainThread() {
    return true;
  }
}
