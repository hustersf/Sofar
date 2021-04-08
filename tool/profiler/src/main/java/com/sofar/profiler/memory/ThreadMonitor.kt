package com.sofar.profiler.memory

import android.util.Log
import com.sofar.profiler.AbsMonitor
import com.sofar.profiler.MonitorManager
import com.sofar.profiler.MonitorType
import java.lang.Exception

class ThreadMonitor : AbsMonitor() {

  private var tag = "ThreadMonitor"
  private var warningCount = 200

  private var count = 0

  private val runnable: Runnable = object : Runnable {
    override fun run() {
      record()
      handler.postDelayed(this, pollInterval())
    }
  }

  override fun onStart() {
    handler.removeCallbacks(runnable)
    handler.postDelayed(runnable, pollInterval())
  }

  override fun onStop() {
    handler.removeCallbacks(runnable)
  }

  override fun type(): MonitorType {
    return MonitorType.THREAD
  }

  override fun pollInterval(): Long {
    return 1000
  }

  private fun record() {
    try {
      dumpThread()
      Log.d(tag, "thread count=$count")
      MonitorManager.threadCallback(count)
    } catch (e: Exception) {
      Log.d(tag, e.toString())
    }
  }

  fun getCount(): Int {
    return count
  }

  private fun dumpThread() {
    val threadSet: Set<Thread> = Thread.getAllStackTraces().keys
    count = threadSet.size
    if (threadSet.size > warningCount) {
      Log.d(tag, getInfo())
    }
  }


  fun getInfo(): String {
    val threadSet: Set<Thread> = Thread.getAllStackTraces().keys
    val sb = StringBuilder()
    for (t in threadSet) {
      sb.append(t.name).append("\n")
    }
    Log.d(tag, "thread count={${threadSet.size}}")
    return sb.toString()
  }

}