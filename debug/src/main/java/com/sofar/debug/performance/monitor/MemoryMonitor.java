package com.sofar.debug.performance.monitor;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.Process;
import android.util.Log;

import com.sofar.debug.performance.PerformanceMonitorManager;

public class MemoryMonitor extends AbsMonitor {

  private static final int SAMPLING_TIME = 1000;
  private static final String TAG = "MemoryMonitor";

  private float mLastMemory;
  ActivityManager activityManager;

  private Runnable runnable = new Runnable() {
    @Override
    public void run() {
      mLastMemory = getMemoryData();
      record();
      handler.postDelayed(this, SAMPLING_TIME);
    }
  };

  public MemoryMonitor() {
    activityManager = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
  }

  @Override
  protected void onStart() {
    handler.postDelayed(runnable, SAMPLING_TIME);
  }

  @Override
  protected void onStop() {
    handler.removeCallbacks(runnable);
  }

  private void record() {
    ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
    activityManager.getMemoryInfo(info);
    float total = info.totalMem / 1024 / 1024;
    float avail = info.availMem / 1024 / 1024;
    Log.d(TAG, "当前使用内存=" + mLastMemory + "MB" + " 总内存=" + total + "MB" + " 可用内存=" + avail + "MB");
    PerformanceMonitorManager.get().memoryCallback(mLastMemory);
  }

  private float getMemoryData() {
    float mem = 0.0F;
    try {
      Debug.MemoryInfo memInfo = null;
      if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
        memInfo = new Debug.MemoryInfo();
        Debug.getMemoryInfo(memInfo);
      } else {
        Debug.MemoryInfo[] memInfos =
          activityManager.getProcessMemoryInfo(new int[]{Process.myPid()});
        if (memInfos != null && memInfos.length > 0) {
          memInfo = memInfos[0];
        }
      }

      int totalPss = memInfo.getTotalPss();
      if (totalPss >= 0) {
        mem = totalPss / 1024.0F;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return mem;
  }
}
