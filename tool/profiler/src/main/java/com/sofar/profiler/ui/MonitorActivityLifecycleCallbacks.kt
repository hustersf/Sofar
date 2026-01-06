package com.sofar.profiler.ui

import android.app.Activity
import android.app.Application
import android.os.Bundle

class MonitorActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

  override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    MonitorViewManager.create(activity)
  }

  override fun onActivityStarted(activity: Activity) {}

  override fun onActivityResumed(activity: Activity) {
    MonitorViewManager.attach(activity)
  }

  override fun onActivityPaused(activity: Activity) {
    MonitorViewManager.detach(activity)
  }

  override fun onActivityStopped(activity: Activity) {}

  override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

  override fun onActivityDestroyed(activity: Activity) {
    MonitorViewManager.destroy(activity)
  }

}