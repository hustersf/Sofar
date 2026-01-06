package com.sofar.profiler

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import java.lang.reflect.Method

fun formatNumber(args: Any?): String {
  return String.format("%.2f", args)
}

fun getMetricsWidth(context: Context): Int {
  val dm = context.resources.displayMetrics
  val screenWidth = dm.widthPixels
  return screenWidth
}


/**
 * 获取屏幕高度px
 */
fun getMetricsHeight(context: Context): Int {
  val dm = context.resources.displayMetrics
  val screenHeight = dm.heightPixels
  return screenHeight
}

@SuppressLint("InternalInsetResource,DiscouragedApi")
fun getStatusBarHeight(context: Context): Int {
  // 获得状态栏高度
  val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
  return context.resources.getDimensionPixelSize(resourceId)
}

@SuppressLint("DiscouragedPrivateApi", "PrivateApi")
fun getProcessName(): String {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    return Application.getProcessName()
  }
  try {
    val method: Method = Class.forName("android.app.ActivityThread")
      .getDeclaredMethod("currentProcessName")
    method.setAccessible(true)
    return (method.invoke(null) as String)
  } catch (e: Throwable) {
    return ""
  }
}
