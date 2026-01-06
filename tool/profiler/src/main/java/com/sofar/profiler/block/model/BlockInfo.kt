package com.sofar.profiler.block.model

import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Locale

class BlockInfo {
  var timeCost: Long = 0
  var threadTimeCost: Long = 0
  var keyClass: String = ""

  var timeStart: String = ""
  var timeEnd: String = ""
  private var threadStackEntries: ArrayList<String> = ArrayList()

  private val timeSb = StringBuilder()
  private val stackSb = StringBuilder()

  fun flushString(): BlockInfo {
    val separator = SEPARATOR
    timeSb.append(KEY_TIME_COST).append(KV).append(timeCost).append(separator)
    timeSb.append(KEY_THREAD_TIME_COST).append(KV).append(threadTimeCost).append(separator)
    timeSb.append(KEY_TIME_COST_START).append(KV).append(timeStart).append(separator)
    timeSb.append(KEY_TIME_COST_END).append(KV).append(timeEnd).append(separator)

    if (threadStackEntries.isNotEmpty()) {
      val temp = StringBuilder()
      for (s in threadStackEntries) {
        temp.append(s)
        temp.append(separator)
      }
      stackSb.append(KEY_STACK).append(KV).append(temp.toString()).append(separator)
    }
    return this
  }


  fun setThreadStackEntries(threadStackEntries: ArrayList<String>): BlockInfo {
    this.threadStackEntries = threadStackEntries
    return this
  }

  fun setMainThreadTimeCost(
    realTimeStart: Long,
    realTimeEnd: Long,
    threadTimeStart: Long,
    threadTimeEnd: Long
  ): BlockInfo {
    timeCost = realTimeEnd - realTimeStart
    threadTimeCost = threadTimeEnd - threadTimeStart
    timeStart = TIME_FORMATTER.format(realTimeStart)
    timeEnd = TIME_FORMATTER.format(realTimeEnd)
    return this
  }

  override fun toString(): String {
    return "${timeSb}\n${stackSb}"
  }

  companion object {
    const val SEPARATOR: String = "\r\n"
    private const val KV = " = "
    private val TIME_FORMATTER = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.CHINESE)
    private const val KEY_TIME_COST = "time"
    private const val KEY_THREAD_TIME_COST = "thread-time"
    private const val KEY_TIME_COST_START = "time-start"
    private const val KEY_TIME_COST_END = "time-end"
    private const val KEY_STACK = "stack"
  }
}
