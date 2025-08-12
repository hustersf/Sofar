package com.sofar.profiler.startup

import android.app.Activity
import android.os.SystemClock
import android.util.Log

/**
 * [StartupTracer] 升级版
 * 业务方无需主动调用相关API,SDK内部统计各个阶段的耗时
 */
internal class StartupTracerV2 {

  companion object {
    @JvmStatic
    fun get(): StartupTracerV2 {
      return Holder.INSTANCE
    }
  }

  private object Holder {
    val INSTANCE: StartupTracerV2 = StartupTracerV2()
  }

  private var applicationStartTime: Long = 0
  private var applicationCost: Long = 0
  private var firstScreenCost: Long = 0
  private var coldCost: Long = 0

  private var activeActivityCount = 0
  private var warmStartUp = false
  private var lastCreateActivity: Long = 0

  fun start() {
    applicationStartTime = time()
  }

  fun onActivityCreated(activity: Activity) {
    if (activeActivityCount == 0) {
      if (applicationCost > 0) {
        warmStartUp = true
        lastCreateActivity = time()
      } else {
        applicationCost = time() - applicationStartTime
      }
    }
    activeActivityCount++
  }

  fun onActivityResumed(activity: Activity) {
    activity.window.decorView.post {
      if (isWarmStartUp()) {
        warmStartUp = false
        val warmCost = time() - lastCreateActivity
      } else if (isColdStartup()) {
        if (firstScreenCost == 0L) {
          firstScreenCost = time() - applicationStartTime
        } else {
          coldCost = time() - applicationStartTime
        }
        if (firstScreenCost > 0 || coldCost > 0) {
          analyse(applicationCost, firstScreenCost, coldCost, false)
        }
      }
    }
  }

  fun onActivityDestroyed(activity: Activity) {
    activeActivityCount--
  }

  private fun isColdStartup(): Boolean {
    return coldCost == 0L
  }

  private fun isWarmStartUp(): Boolean {
    return warmStartUp
  }

  private fun time(): Long {
    return SystemClock.elapsedRealtime()
  }

  private fun analyse(
    applicationCost: Long,
    firstScreenCost: Long,
    allCost: Long,
    warmStartUp: Boolean
  ) {
    if (!warmStartUp) {
      Log.d(
        "StartupTracer", "applicationCost=$applicationCost,"
            + "firstScreenCost=$firstScreenCost,"
            + "allCost=$allCost"
      )
    }
  }

}