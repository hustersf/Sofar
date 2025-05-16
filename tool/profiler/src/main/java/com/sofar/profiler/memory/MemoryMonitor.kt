package com.sofar.profiler.memory

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import android.util.Log
import com.sofar.profiler.AbsMonitor
import com.sofar.profiler.MonitorManager
import com.sofar.profiler.MonitorType
import com.sofar.profiler.formatNumber
import java.io.File
import java.lang.Exception

class MemoryMonitor : AbsMonitor() {

  private var tag = "MemoryMonitor"
  var activityManager: ActivityManager = appContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

  var memInfo = StringBuffer()

  private val runnable: Runnable = object : Runnable {
    override fun run() {
      memInfo.setLength(0)
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
    return MonitorType.MEMORY
  }

  fun getMemoryInfo(): String {
    return memInfo.toString()
  }

  private fun record() {
    try {
      javaHeap()
      totalMemory()
      readFile()
      Log.d(tag, memInfo.toString())
      MonitorManager.memoryCallback(memInfo.toString())
    } catch (e: Exception) {
      Log.d(tag, e.toString())
    }
  }

  private fun javaHeap() {
    var max = Runtime.getRuntime().maxMemory()
    var total = Runtime.getRuntime().totalMemory()
    var free = Runtime.getRuntime().freeMemory()

    var use = total - free
    var ratio = formatNumber(1.0f * 100 * use / max)

    memInfo.append("java heap use(MB)=")
    memInfo.append(formatNumber(1.0f * use / 1024 / 1024))
    memInfo.append("($ratio%)")
    memInfo.append("\n")
  }

  private fun totalMemory() {
    var mem = Debug.MemoryInfo()
    Debug.getMemoryInfo(mem)
    var totalPss = mem.totalPss
    var nativePss = mem.nativePss

    memInfo.append("total pss(MB)=")
    memInfo.append(formatNumber(1.0f * totalPss / 1024))
    memInfo.append("\n")
    memInfo.append("native pss(MB)=")
    memInfo.append(formatNumber(1.0f * nativePss / 1024))
    memInfo.append("\n")

    val info = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(info)
    val total = info.totalMem / 1024 / 1024.toFloat()
    val avail = info.availMem / 1024 / 1024.toFloat()
    memInfo.append("总RAM容量(MB)=${formatNumber(total)}")
    memInfo.append("\n")
    memInfo.append("剩余RAM(MB)=${formatNumber(avail)}")
    memInfo.append("\n")
  }

  private fun readFile() {
    var lines = File("/proc/$pid/status").readLines()
    lines.forEach {
      //  Log.d(tag, "line=$it")
      if (it.startsWith("VmSize")) {
        var params = it.split(Regex("\\s+"))
        if (params.size > 1) {
          memInfo.append("VmSize(MB)=")
          memInfo.append(params[1].toInt() / 1024)
          memInfo.append("\n")
        }
      }
    }
  }

}