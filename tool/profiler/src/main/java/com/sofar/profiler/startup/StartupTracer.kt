package com.sofar.profiler.startup

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import com.sofar.profiler.MonitorManager
import com.sofar.profiler.startup.model.StartupInfo

/**
 * 该类不再使用，SDK内部使用[StartupTracerV2]
 * 业务方需要主动调用相关API
 * 1.[onApplicationCreateStart]
 * 2.[onApplicationCreateEnd]
 * 3.[onSplashActivityCreated]
 * 4.[onActivityFocused]
 */
class StartupTracer : Application.ActivityLifecycleCallbacks {

  companion object {
    @JvmStatic
    fun get(): StartupTracer {
      return Holder.INSTANCE
    }
  }

  private object Holder {
    val INSTANCE: StartupTracer = StartupTracer()
  }

  private var applicationCost: Long = 0
  private var firstScreenCost: Long = 0
  private var coldCost: Long = 0

  private var activeActivityCount = 0
  private var warmStartUp = false
  private var splashActivityName = ""
  private var splashActivityShowed = false
  private var lastCreateActivity: Long = 0

  private var applicationStartTime: Long = 0

  /**
   * 在 Application.onCreate 第一行代码调用
   */
  fun onApplicationCreateStart(appContext: Application) {
    applicationStartTime = time()
    appContext.registerActivityLifecycleCallbacks(this)
    Log.d("StartupTracer", "onApplicationCreateStart")
  }

  /**
   * 在 Application.onCreate 最后一行代码调用
   */
  fun onApplicationCreateEnd() {
    applicationCost = time() - applicationStartTime
    Log.d("StartupTracer", "onApplicationCreateEnd")
  }

  /**
   * 在启动页 onCreate 生命周期调用
   */
  fun onSplashActivityCreated(activity: Activity) {
    splashActivityName = activity.javaClass.name
  }

  /**
   * 在启动页,主页的 onWindowFocusChanged 中当 hasFocus==true时 调用
   * 在启动页,主页的 onWindowFocusChanged 中当 hasFocus==true时 调用
   */
  fun onActivityFocused(activity: Activity) {
    if (isColdStartup()) {
      if (firstScreenCost == 0L) {
        this.firstScreenCost = time() - applicationStartTime
      }
      if (splashActivityShowed) {
        coldCost = time() - applicationStartTime
      } else {
        var activityName = activity.javaClass.name;
        if (splashActivityName.isEmpty()) {
          coldCost = firstScreenCost
        } else if (activityName == splashActivityName) {
          splashActivityShowed = true
        }
      }
      if (coldCost > 0) {
        analyse(applicationCost, firstScreenCost, coldCost, false)
      }
    } else if (isWarmStartUp()) {
      warmStartUp = false
      val warmCost = time() - lastCreateActivity
    }
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

  private fun activityId(activity: Activity): String {
    return "${activity.javaClass.name}@${activity.hashCode()}"
  }

  override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    if (activeActivityCount == 0 && coldCost > 0) {
      warmStartUp = true
      lastCreateActivity = time()
    }
    activeActivityCount++
  }

  override fun onActivityDestroyed(activity: Activity) {
    activeActivityCount--
  }

  override fun onActivityStarted(activity: Activity) {}

  override fun onActivityResumed(activity: Activity) {}

  override fun onActivityPaused(activity: Activity) {}

  override fun onActivityStopped(activity: Activity) {}

  override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

  private fun analyse(
    applicationCost: Long,
    firstScreenCost: Long,
    allCost: Long,
    warmStartUp: Boolean
  ) {
    if (!warmStartUp) {
      var appStartInfo = StartupInfo(applicationCost, firstScreenCost, allCost)
      Log.d("StartupTracer", appStartInfo.toString())
      MonitorManager.appStartCallback(appStartInfo)
    }
  }

}