package com.sofar.profiler.memory

import android.os.Build
import android.os.Debug
import android.util.Log
import com.sofar.profiler.AbsMonitor
import com.sofar.profiler.MonitorManager
import com.sofar.profiler.MonitorType
import com.sofar.profiler.memory.model.MemoryInfo
import java.lang.Exception

class MemoryMonitor : AbsMonitor() {

  private var tag = "MemoryMonitor"
  private lateinit var memInfo: MemoryInfo

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
    return MonitorType.MEMORY
  }

  fun getMemoryInfo(): String {
    return memInfo.toString()
  }

  private fun record() {
    try {
      pssMemory()
      Log.d(tag, memInfo.toString())
      MonitorManager.memoryCallback(memInfo)
    } catch (e: Exception) {
      Log.d(tag, e.toString())
    }
  }

  private fun pssMemory() {
    var mem = Debug.MemoryInfo()
    Debug.getMemoryInfo(mem)

    var pssTotalK = mem.totalPss
    var pssJavaK = -1
    var pssNativeK = -1
    var pssGraphicK = -1
    var pssStackK = -1
    var pssCodeK = -1

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      mem.memoryStats.apply {
        fun Map<String, String>.getInt(key: String) = get(key)?.toInt() ?: -1
        pssJavaK = getInt("summary.java-heap")
        pssNativeK = getInt("summary.native-heap")
        pssCodeK = getInt("summary.code")
        pssStackK = getInt("summary.stack")
        pssGraphicK = getInt("summary.graphics")
      }
    } else {
      mem.apply {
        pssJavaK = dalvikPrivateDirty
        pssNativeK = nativePrivateDirty
      }
    }

    memInfo = MemoryInfo(pssTotalK, pssJavaK, pssNativeK, pssGraphicK, pssStackK, pssCodeK)
  }

}