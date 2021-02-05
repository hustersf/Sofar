package com.sofar.debug.performance.monitor;

import android.app.Application;
import android.os.Handler;
import androidx.annotation.NonNull;

import com.sofar.debug.performance.PerformanceMonitorManager;

public abstract class AbsMonitor implements IMonitor {

  protected boolean started;

  @NonNull
  protected Handler handler;
  @NonNull
  protected Handler UIHandler;
  @NonNull
  protected Application appContext;

  public AbsMonitor() {
    handler = PerformanceMonitorManager.get().getHandler();
    UIHandler = PerformanceMonitorManager.get().getUIHandler();
    appContext = PerformanceMonitorManager.get().getAppContext();
  }

  @Override
  public void start() {
    if (started) {
      return;
    }

    started = true;
    onStart();
  }

  @Override
  public void stop() {
    if (!started) {
      return;
    }

    started = false;
    onStop();
  }

  protected abstract void onStart();

  protected abstract void onStop();
}
