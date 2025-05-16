package com.sofar.profiler.memory

import android.util.Log
import com.sofar.profiler.AbsMonitor
import com.sofar.profiler.MonitorManager
import com.sofar.profiler.MonitorType
import java.io.File
import java.lang.Exception

class FDMonitor : AbsMonitor() {

  private var tag = "FDMonitor"
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
    return MonitorType.FD
  }

  private fun record() {
    try {
      readFile()
      Log.d(tag, "use fd=$count")
      MonitorManager.fdCallback(count)
    } catch (e: Exception) {
      Log.d(tag, e.toString())
    }
  }

  fun getCount(): Int {
    return count
  }

  private fun readFile() {
    var limits = File("/proc/$pid/limits").readLines()
    limits.forEach {
      //  Log.d(tag, "line=$it")
      if (it.startsWith("Max open files")) {
        var params = it.split(Regex("\\s+"))
        if (params.size > 4) {
          Log.d(tag, "max fd=${params[4]}")
        }
      }
    }

    var fdDir = File("/proc/$pid/fd")
    count = fdDir.listFiles().size
  }
}