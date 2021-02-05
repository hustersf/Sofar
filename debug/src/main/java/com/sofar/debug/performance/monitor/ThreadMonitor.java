package com.sofar.debug.performance.monitor;

import java.util.Set;

import android.util.Log;

import com.sofar.debug.performance.PerformanceMonitorManager;

public class ThreadMonitor extends AbsMonitor {

  private static final int DUMP_INTERVAL = 10 * 1000;
  private static final String TAG = "ThreadMonitor";
  private static final int WARNING_COUNT = 400;

  private int mLastThreadCount;

  private Runnable runnable = new Runnable() {
    @Override
    public void run() {
      dumpThread();
      record();
      handler.postDelayed(this, DUMP_INTERVAL);
    }
  };

  private void dumpThread() {
    Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
    mLastThreadCount = threadSet.size();
    if (threadSet.size() > WARNING_COUNT) {
      Log.d(TAG, "！警告，当前线程数过多 " + mLastThreadCount);

      StringBuilder sb = new StringBuilder();
      for (Thread t : threadSet) {
        sb.append(t.getName())
          .append("\t");
      }
      Log.d(TAG, "doDump: current threads : \r\n" + sb.toString());
    }
  }


  @Override
  protected void onStart() {
    handler.postDelayed(runnable, DUMP_INTERVAL);
  }

  @Override
  protected void onStop() {
    handler.removeCallbacks(runnable);
  }

  private void record() {
    Log.d(TAG, "当前线程数=" + mLastThreadCount);
    PerformanceMonitorManager.get().threadCallback(mLastThreadCount);
  }

}
