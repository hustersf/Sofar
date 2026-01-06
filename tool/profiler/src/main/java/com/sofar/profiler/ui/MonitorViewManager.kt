package com.sofar.profiler.ui

import android.app.Activity
import com.sofar.profiler.ui.floating.FloatingWidget

object MonitorViewManager {

  private val activityViewMap: MutableMap<Activity, FloatingWidget> = HashMap()

  fun create(activity: Activity) {
    if (!activityViewMap.containsKey(activity)) {
      var widget = FloatingWidget(activity)
      widget.setScreenRatio(0.8f, 0.8f)
      var monitorLayout = MonitorLayout(activity)
      widget.addView(monitorLayout)
      activityViewMap[activity] = widget
    }
  }

  fun attach(activity: Activity) {
    activityViewMap[activity]?.attach()
  }

  fun detach(activity: Activity) {
    activityViewMap[activity]?.detach()
  }

  fun destroy(activity: Activity) {
    activityViewMap.remove(activity)
  }
}