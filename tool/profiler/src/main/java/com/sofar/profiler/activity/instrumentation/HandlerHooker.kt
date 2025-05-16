package com.sofar.profiler.activity.instrumentation

import android.app.Application
import android.os.Build
import android.os.Handler
import com.sofar.profiler.refelection.ReflectUtils
import com.sofar.profiler.refelection.Reflection

object HandlerHooker {
  private const val TAG = "HandlerHooker"

  //是否已经hook成功
  var isHookSucceed: Boolean = false
    private set

  fun doHook(app: Application) {
    try {
      if (isHookSucceed) {
        return
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        //解锁调用系统隐藏api的权限
        Reflection.unseal(app)
      }
      //hook ActivityThread的Instrumentation
      hookInstrumentation()
      isHookSucceed = true
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }


  /**
   * hook ActivityThread的Instrumentation
   */
  private fun hookInstrumentation() {
    //得到ActivityThread对象
    val currentActivityThreadObj: Any =
      ReflectUtils.reflect("android.app.ActivityThread").method("currentActivityThread").get()
    //ActivityThread对象的 mH变量
    val handlerObj: Handler = ReflectUtils.reflect(currentActivityThreadObj).field("mH").get()
    val handCallbackObj: Handler.Callback? =
      ReflectUtils.reflect(handlerObj).field("mCallback").get()
    val proxyMHCallback = ProxyHandlerCallback(handCallbackObj, handlerObj)
    //替换mCallback 对象
    ReflectUtils.reflect(handlerObj).field("mCallback", proxyMHCallback)
  }
}
