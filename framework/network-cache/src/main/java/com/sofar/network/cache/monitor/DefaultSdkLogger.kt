package com.sofar.network.cache.monitor

import android.util.Log

/**
 * SDK 默认提供的 Android Log 打印器。
 *
 * @param isDebug 是否开启调试模式。为 false 时线上环境保持绝对静默，不打印任何日志。
 */
class DefaultSdkLogger(private val isDebug: Boolean) : ISdkLogger {

  private companion object {
    const val PREFIX = "NetworkCache"
  }

  override fun d(tag: String, msg: String) {
    if (isDebug) {
      Log.d(PREFIX, "[$tag] $msg")
    }
  }

  override fun w(tag: String, msg: String) {
    if (isDebug) {
      Log.w(PREFIX, "[$tag] $msg")
    }
  }

  override fun e(tag: String, msg: String, throwable: Throwable?) {
    if (isDebug) {
      Log.e(PREFIX, "[$tag] $msg", throwable)
    }
  }
}
