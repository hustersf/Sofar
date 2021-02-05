package com.sofar.debug.performance.monitor;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * 待补充
 */
public class FDMonitor extends AbsMonitor {

  private static final int SAMPLING_TIME = 1000;
  private static final String TAG = "FDMonitor";

  private RandomAccessFile mAppFDFile;

  private int mLastFD;

  private Runnable runnable = new Runnable() {
    @Override
    public void run() {
      mLastFD = getFDData();
      record();
      handler.postDelayed(this, SAMPLING_TIME);
    }
  };


  @Override
  protected void onStart() {
    handler.postDelayed(runnable, SAMPLING_TIME);
  }

  @Override
  protected void onStop() {
    handler.removeCallbacks(runnable);
  }

  public int getFDData() {
    int count = 0;
    try {
      File fddir = new File("/proc/self/fd");
      for (File ff : fddir.listFiles()) {
        count += 1;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return count;
  }

  private void record() {
  }

}
