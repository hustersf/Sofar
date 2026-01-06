package com.sofar.profiler.block.core

import android.os.Looper
import android.util.Log
import com.sofar.profiler.MonitorManager
import com.sofar.profiler.block.model.BlockInfo

class BlockMonitorManager {

  private var looperMonitor: LooperMonitor? = null
  private var isRunning = false

  companion object {
    private const val TAG: String = "BlockMonitorManager"

    @JvmStatic
    fun get(): BlockMonitorManager {
      return Holder.INSTANCE
    }
  }

  private object Holder {
    val INSTANCE: BlockMonitorManager = BlockMonitorManager()
  }

  fun start(blockThresholdMillis: Int = 0) {
    if (isRunning) {
      return
    }
    isRunning = true
    if (looperMonitor == null) {
      looperMonitor = LooperMonitor()
      looperMonitor!!.init(blockThresholdMillis)
    }
    Looper.getMainLooper().setMessageLogging(looperMonitor)
  }

  fun stop() {
    if (!isRunning) {
      return
    }
    isRunning = false
    Looper.getMainLooper().setMessageLogging(null)
    looperMonitor?.shutDown()
    looperMonitor = null
  }

  fun notifyBlockEvent(blockInfo: BlockInfo) {
    Log.d(TAG, "------------------notifyBlockEvent----------------")
    Log.d(TAG, blockInfo.toString())
    MonitorManager.blockCallback(blockInfo)
  }

}