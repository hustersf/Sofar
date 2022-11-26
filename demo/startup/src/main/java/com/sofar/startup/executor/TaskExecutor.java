package com.sofar.startup.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskExecutor {
  public static ExecutorService runExecutor = Executors.newFixedThreadPool(3);
}
