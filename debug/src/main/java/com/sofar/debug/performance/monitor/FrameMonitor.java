package com.sofar.debug.performance.monitor;

import android.util.Log;
import android.view.Choreographer;

import com.sofar.debug.performance.PerformanceMonitorManager;

/**
 * 监控fps（Frames Per Second）
 */
public class FrameMonitor extends AbsMonitor {

  private static final String TAG = "FrameMonitor";

  private static final int FPS_SAMPLING_TIME = 1000;
  private static final int MAX_FRAME_RATE = 60;
  private int mLastFrameRate = MAX_FRAME_RATE;

  private FrameRateRunnable mRateRunnable = new FrameRateRunnable();

  private class FrameRateRunnable implements Runnable, Choreographer.FrameCallback {
    private int totalFramesPerSecond;

    @Override
    public void doFrame(long frameTimeNanos) {
      totalFramesPerSecond++;
      Choreographer.getInstance().postFrameCallback(this);
    }

    @Override
    public void run() {
      mLastFrameRate = totalFramesPerSecond;
      record();
      totalFramesPerSecond = 0;
      UIHandler.postDelayed(this, FPS_SAMPLING_TIME);
    }
  }


  @Override
  protected void onStart() {
    UIHandler.postDelayed(mRateRunnable, FPS_SAMPLING_TIME);
    Choreographer.getInstance().postFrameCallback(mRateRunnable);
  }

  @Override
  protected void onStop() {
    Choreographer.getInstance().removeFrameCallback(mRateRunnable);
    UIHandler.removeCallbacks(mRateRunnable);
  }

  private void record() {
    Log.d(TAG, "帧率=" + mLastFrameRate);
    PerformanceMonitorManager.get().frameCallback(mLastFrameRate);
  }

}
