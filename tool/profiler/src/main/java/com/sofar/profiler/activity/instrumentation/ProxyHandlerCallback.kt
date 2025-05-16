package com.sofar.profiler.activity.instrumentation

import android.os.Handler
import android.os.Message
import android.text.TextUtils
import com.sofar.profiler.activity.ActivityTracer
import com.sofar.profiler.refelection.Reflector

internal class ProxyHandlerCallback(
  private val oldCallback: Handler.Callback?,
  val handler: Handler
) : Handler.Callback {
  override fun handleMessage(msg: Message): Boolean {
    val msgType = preDispatch(msg)
    if (oldCallback != null && oldCallback.handleMessage(msg)) {
      postDispatch(msgType)
      return true
    }
    handler.handleMessage(msg)
    postDispatch(msgType)
    return true
  }

  private fun preDispatch(msg: Message): Int {
    when (msg.what) {
      LAUNCH_ACTIVITY -> ActivityTracer.get().onActivityLaunch()
      PAUSE_ACTIVITY -> ActivityTracer.get().onActivityPause()
      EXECUTE_TRANSACTION -> return handlerActivity(msg)
      else -> {}
    }
    return msg.what
  }

  private fun handlerActivity(msg: Message): Int {
    val obj = msg.obj

    val activityCallback: Any? =
      Reflector.QuietReflector.with(obj).method("getLifecycleStateRequest").call()
    if (activityCallback != null) {
      val transactionName = activityCallback.javaClass.canonicalName
      if (TextUtils.equals(transactionName, LAUNCH_ITEM_CLASS)) {
        ActivityTracer.get().onActivityLaunch()
        return LAUNCH_ACTIVITY
      } else if (TextUtils.equals(transactionName, PAUSE_ITEM_CLASS)) {
        ActivityTracer.get().onActivityPause()
        return PAUSE_ACTIVITY
      }
    }
    return msg.what
  }

  private fun postDispatch(msgType: Int) {
    when (msgType) {
      LAUNCH_ACTIVITY -> ActivityTracer.get().onActivityLaunched()
      PAUSE_ACTIVITY -> ActivityTracer.get().onActivityPaused()
      else -> {}
    }
  }

  companion object {
    private const val TAG = "ProxyHandlerCallback"

    /**
     * Android 28开始 变量从110开始
     */
    private const val LAUNCH_ACTIVITY = 100
    private const val PAUSE_ACTIVITY = 101
    private const val EXECUTE_TRANSACTION = 159
    private const val LAUNCH_ITEM_CLASS = "android.app.servertransaction.ResumeActivityItem"
    private const val PAUSE_ITEM_CLASS = "android.app.servertransaction.PauseActivityItem"
  }
}
