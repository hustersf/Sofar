package com.sofar.profiler.block.core

import android.os.SystemClock
import android.util.Printer
import com.sofar.profiler.block.model.BlockInfo
import com.sofar.profiler.config.DEFAULT_BLOCK_THRESHOLD_MILL

class LooperMonitor : Printer {

  private var startTime: Long = 0
  private var startThreadTime: Long = 0
  private var printingStarted = false

  private var stackSampler = StackSampler()
  private var blockThresholdMillis: Int = DEFAULT_BLOCK_THRESHOLD_MILL

  fun init(time: Int = DEFAULT_BLOCK_THRESHOLD_MILL) {
    if (time <= 0) {
      blockThresholdMillis = DEFAULT_BLOCK_THRESHOLD_MILL
    } else {
      blockThresholdMillis = time
    }
    stackSampler.init((blockThresholdMillis * 0.8f).toInt())
  }

  override fun println(x: String) {
    if (!printingStarted) {
      startTime = System.currentTimeMillis()
      startThreadTime = SystemClock.currentThreadTimeMillis()
      printingStarted = true
      stackSampler.startDump()
    } else {
      val endTime = System.currentTimeMillis()
      val endThreadTime = SystemClock.currentThreadTimeMillis()
      printingStarted = false
      if (isBlock(endTime)) {
        val entries: ArrayList<String> = stackSampler.getThreadStackEntries(startTime, endTime)
        if (entries.size > 0) {
          val blockInfo = BlockInfo()
            .setMainThreadTimeCost(startTime, endTime, startThreadTime, endThreadTime)
            .setThreadStackEntries(entries)
            .flushString()
          BlockMonitorManager.get().notifyBlockEvent(blockInfo)
        }
      }
      stackSampler.stopDump()
    }
  }

  private fun isBlock(endTime: Long): Boolean {
    return endTime - startTime > blockThresholdMillis
  }

  fun shutDown() {
    stackSampler.shutDown()
  }
}