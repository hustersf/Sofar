package com.sofar.debug.performance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Application;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import androidx.annotation.NonNull;

import com.sofar.debug.performance.monitor.CpuMonitor;
import com.sofar.debug.performance.monitor.FDMonitor;
import com.sofar.debug.performance.monitor.FrameMonitor;
import com.sofar.debug.performance.monitor.IMonitor;
import com.sofar.debug.performance.monitor.MemoryMonitor;
import com.sofar.debug.performance.monitor.MonitorCallback;
import com.sofar.debug.performance.monitor.MonitorType;
import com.sofar.debug.performance.monitor.ThreadMonitor;

public class PerformanceMonitorManager {

  @NonNull
  Handler UIHandler;
  @NonNull
  Handler handler;

  Application appContext;

  List<MonitorCallback> callbacks = new ArrayList<>();
  HashMap<Integer, IMonitor> monitorMap = new HashMap<>();

  volatile boolean init = false;

  private static class Inner {
    private static PerformanceMonitorManager INSTANCE = new PerformanceMonitorManager();
  }

  public static PerformanceMonitorManager get() {
    return Inner.INSTANCE;
  }

  private PerformanceMonitorManager() {
    UIHandler = new Handler(Looper.getMainLooper());
    HandlerThread handlerThread = new HandlerThread("performance-monitor");
    handlerThread.start();
    handler = new Handler(handlerThread.getLooper());
  }

  public void init(@NonNull Application app) {
    if (init) {
      return;
    }

    init = true;
    this.appContext = app;
    initMonitor();
  }

  public void addMonitorCallback(MonitorCallback callback) {
    if (!callbacks.contains(callback)) {
      callbacks.add(callback);
    }
  }

  public void removeMonitorCallback(MonitorCallback callback) {
    if (callbacks.contains(callback)) {
      callbacks.remove(callback);
    }
  }

  public void start() {
    for (int key : monitorMap.keySet()) {
      start(key);
    }
  }

  public void start(@MonitorType int type) {
    IMonitor monitor = monitorMap.get(type);
    if (monitor != null) {
      monitor.start();
    }
  }

  public void stop() {
    for (int key : monitorMap.keySet()) {
      stop(key);
    }
  }

  public void stop(@MonitorType int type) {
    IMonitor monitor = monitorMap.get(type);
    if (monitor != null) {
      monitor.stop();
    }
  }

  public void frameCallback(int frameRate) {
    for (MonitorCallback callback : callbacks) {
      callback.onFrameRate(frameRate);
    }
  }

  public void cpuCallback(float cpuRate) {
    for (MonitorCallback callback : callbacks) {
      callback.onCpuRate(cpuRate);
    }
  }

  public void threadCallback(int count) {
    for (MonitorCallback callback : callbacks) {
      callback.onThreadCount(count);
    }
  }

  public void memoryCallback(float size) {
    for (MonitorCallback callback : callbacks) {
      callback.onMemory(size);
    }
  }

  public void fdCallback(int count) {
    for (MonitorCallback callback : callbacks) {
      callback.onFDCount(count);
    }
  }

  private void initMonitor() {
    monitorMap.put(MonitorType.FPS, new FrameMonitor());
    monitorMap.put(MonitorType.CPU, new CpuMonitor());
    monitorMap.put(MonitorType.MEMORY, new MemoryMonitor());
    monitorMap.put(MonitorType.THREAD, new ThreadMonitor());
    //  monitorMap.put(MonitorType.FD, new FDMonitor());
  }

  @NonNull
  public Handler getHandler() {
    return handler;
  }

  @NonNull
  public Handler getUIHandler() {
    return UIHandler;
  }

  public Application getAppContext() {
    return appContext;
  }
}
