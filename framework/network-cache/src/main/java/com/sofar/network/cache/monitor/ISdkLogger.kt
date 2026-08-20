package com.sofar.network.cache.monitor

interface ISdkLogger {
  fun d(tag: String, msg: String)
  fun w(tag: String, msg: String)
  fun e(tag: String, msg: String, throwable: Throwable? = null)
}
