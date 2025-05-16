package com.sofar.profiler.frame

import android.util.Log
import android.view.Choreographer
import com.sofar.profiler.AbsMonitor
import com.sofar.profiler.MonitorManager
import com.sofar.profiler.MonitorType

class FrameMonitor : AbsMonitor() {

  private var tag = "FrameMonitor"
  private val FPS_SAMPLING_TIME = 1000L
  private var frameRate = 60

  private val runnable = FrameRateRunnable()

  private inner class FrameRateRunnable : Runnable, Choreographer.FrameCallback {
    private var totalFramesPerSecond = 0
    override fun doFrame(frameTimeNanos: Long) {
      totalFramesPerSecond++
      Choreographer.getInstance().postFrameCallback(this)
    }

    override fun run() {
      frameRate = totalFramesPerSecond
      record()
      totalFramesPerSecond = 0
      UIHandler.postDelayed(this, FPS_SAMPLING_TIME)
    }
  }

  override fun onStart() {
    UIHandler.postDelayed(runnable, FPS_SAMPLING_TIME)
    Choreographer.getInstance().postFrameCallback(runnable)
  }

  override fun onStop() {
    Choreographer.getInstance().removeFrameCallback(runnable)
    UIHandler.removeCallbacks(runnable)
  }

  override fun type(): MonitorType {
    return MonitorType.FPS
  }

  fun record() {
    Log.d(tag, "fps=$frameRate")
    MonitorManager.frameCallback(frameRate)
  }

  fun getFPS(): Int {
    return frameRate
  }
}