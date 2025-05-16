package com.sofar.profiler.activity

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle

class ActivityLifecycleImpl : Application.ActivityLifecycleCallbacks {

  private var pages: MutableList<Activity> = ArrayList()

  fun init(appContext: Application) {
    appContext.registerActivityLifecycleCallbacks(this)
  }

  override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    pages.add(activity)
  }

  override fun onActivityStarted(activity: Activity) {}

  override fun onActivityResumed(activity: Activity) {}

  override fun onActivityPaused(activity: Activity) {}

  override fun onActivityStopped(activity: Activity) {}

  override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

  override fun onActivityDestroyed(activity: Activity) {
    pages.remove(activity)
  }

  /**
   * @return 获取栈顶Activity
   */
  fun topActivity(): Activity? {
    var index = pages.size - 1
    if (index >= 0) {
      return pages[index]
    }
    return null
  }

  fun topAliveActivity(): Activity? {
    for (i in pages.lastIndex downTo 0) {
      var activity = pages[i]
      if (!isActivityAlive(activity)) {
        continue
      }
      return activity
    }
    return null
  }

  fun isActivityAlive(activity: Activity?): Boolean {
    return activity != null && !activity.isFinishing
        && (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 || !activity.isDestroyed)
  }

  companion object {
    @JvmStatic
    fun get(): ActivityLifecycleImpl {
      return Holder.INSTANCE
    }
  }

  // 静态内部类 Holder
  private object Holder {
    val INSTANCE: ActivityLifecycleImpl = ActivityLifecycleImpl()
  }

}