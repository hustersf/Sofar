package com.sofar.profiler.startup

import android.app.Activity
import android.os.SystemClock
import android.util.Log
import com.sofar.profiler.MonitorManager
import com.sofar.profiler.startup.model.StartupInfo

/**
 * [StartupTracer] 升级版
 * 业务方无需主动调用相关API,SDK内部统计各个阶段的耗时
 */
internal class StartupTracerV2 {

  private var activeActivityCount = 0
  private var startupInfo: StartupInfo? = null
  private var hasSplashActivity = true

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

  private var warmStartUp = false
  private var lastCreateActivity: Long = 0

  fun start(splashActivity: Boolean = true) {
    applicationStartTime = time()
    this.hasSplashActivity = splashActivity
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
    render(activity)
  }

  fun onActivityDestroyed(activity: Activity) {
    activeActivityCount--
  }

  private fun time(): Long {
    return SystemClock.elapsedRealtime()
  }

  private fun render(activity: Activity) {
    activity.window.decorView.post(Runnable {
      if (isWarmStartUp()) {
        //当前暂时不统计温启动,只统计冷启动
        warmStartUp = false
        val warmCost = time() - lastCreateActivity
      } else if (isColdStartup()) {
        if (firstScreenCost == 0L) {
          firstScreenCost = time() - applicationStartTime
        } else {
          coldCost = time() - applicationStartTime
        }
        if (!hasSplashActivity) {
          coldCost = firstScreenCost
        }
        if (coldCost > 0) {
          analyse(applicationCost, firstScreenCost, coldCost, false)
        }
      }
    })
  }

  private fun isColdStartup(): Boolean {
    return coldCost == 0L
  }

  private fun isWarmStartUp(): Boolean {
    return warmStartUp
  }

  private fun analyse(
    applicationCost: Long,
    firstScreenCost: Long,
    allCost: Long,
    warmStartUp: Boolean
  ) {
    if (applicationStartTime <= 0) {
      return
    }
    if (!warmStartUp) {
      startupInfo = StartupInfo(applicationCost, firstScreenCost, allCost)
      startupInfo?.let {
        Log.d("StartupTracerV2", it.toString())
        MonitorManager.appStartCallback(it)
      }
    }
  }

  fun getStartupInfo(): StartupInfo? {
    return startupInfo
  }

}