package com.sofar.profiler

import android.app.Application
import android.os.Handler

abstract class AbsMonitor : IMonitor {

  protected var started = false

  protected var handler: Handler = MonitorManager.monitorHandler
  protected var UIHandler: Handler = MonitorManager.UIHandler
  protected var appContext: Application = MonitorManager.appContext
  protected var pid = MonitorManager.pid.toString()


  override fun start() {
    if (started) {
      return
    }
    started = true
    onStart()
  }

  override fun stop() {
    if (!started) {
      return
    }
    started = false
    onStop()
  }

  protected abstract fun onStart()

  protected abstract fun onStop()
}