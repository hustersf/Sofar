package com.sofar.profiler.activity

import android.app.Activity
import android.app.Application
import android.os.SystemClock
import android.util.Log
import com.sofar.profiler.activity.instrumentation.HandlerHooker

class ActivityTracer private constructor() {

  private var startTime: Long = 0
  private var pauseCostTime: Long = 0
  private var launchStartTime: Long = 0
  private var launchCostTime: Long = 0
  private var renderStartTime: Long = 0
  private var renderCostTime: Long = 0
  private var totalCostTime: Long = 0
  private var otherCostTime: Long = 0

  private var previousActivity: String? = null
  private var previousActivityAlive: Boolean = true
  private var currentActivity: String? = null
  private var timeInfos: MutableList<ActivityTimeInfo> = ArrayList()

  companion object {
    private const val TAG: String = "ActivityTracer"

    @JvmStatic
    fun get(): ActivityTracer {
      return Holder.INSTANCE
    }
  }

  // 静态内部类 Holder
  private object Holder {
    val INSTANCE: ActivityTracer = ActivityTracer()
  }

  fun init(appContext: Application){
    ActivityLifecycleImpl.get().init(appContext)
    HandlerHooker.doHook(appContext)
  }

  fun onActivityPause() {
    Log.d(TAG, "onActivityPause")
    initTime()
    previousActivity = null
    val activity: Activity? = ActivityLifecycleImpl.get().topActivity()
    if (activity != null) {
      previousActivity = activity.javaClass.canonicalName
      previousActivityAlive = ActivityLifecycleImpl.get().isActivityAlive(activity)
    }
  }

  fun onActivityPaused() {
    Log.d(TAG, "onActivityPaused")
    pauseCostTime = time() - startTime
  }


  fun onActivityLaunch() {
    Log.d(TAG, "onActivityLaunch")
    // 可能不走pause，直接打开新页面，比如从后台点击通知栏
    if (startTime == 0L) {
      initTime()
    }
    launchStartTime = time()
    launchCostTime = 0
  }

  fun onActivityLaunched() {
    Log.d(TAG, "onActivityLaunched")
    launchCostTime = time() - launchStartTime
    render()
  }

  private fun render() {
    Log.d(TAG, "render")
    renderStartTime = time()
    val activity: Activity? = ActivityLifecycleImpl.get().topAliveActivity()
    if (activity?.window != null) {
      currentActivity = activity.javaClass.canonicalName
      activity.window.decorView.post(Runnable { renderEnd() })
    } else {
      renderEnd()
    }
  }

  private fun renderEnd() {
    Log.d(TAG, "renderEnd")
    renderCostTime = time() - renderStartTime
    totalCostTime = time() - startTime
    otherCostTime = totalCostTime - renderCostTime - pauseCostTime - launchCostTime
    report()
  }

  private fun report() {
    val timeInfo = ActivityTimeInfo()
    timeInfo.title = "$previousActivity -> $currentActivity"
    timeInfo.back = !previousActivityAlive
    timeInfo.launchCost = launchCostTime
    timeInfo.pauseCost = pauseCostTime
    timeInfo.renderCost = renderCostTime
    timeInfo.totalCost = totalCostTime
    timeInfo.otherCost = otherCostTime
    timeInfos.add(timeInfo)
    Log.d(TAG, "report=${timeInfo}")
  }

  private fun initTime() {
    startTime = time()
    pauseCostTime = 0
    renderCostTime = 0
    otherCostTime = 0
    launchCostTime = 0
    launchStartTime = 0
    totalCostTime = 0
  }

  private fun time(): Long {
    return SystemClock.elapsedRealtime()
  }
}