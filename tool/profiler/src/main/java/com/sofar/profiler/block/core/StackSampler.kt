package com.sofar.profiler.block.core

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 堆栈信息采集类
 */
class StackSampler {
  private val running = AtomicBoolean(false)
  private var sackThread: HandlerThread? = null
  private var stackHandler: Handler? = null

  private val stackMap = LinkedHashMap<Long, String>()
  private var filterCache: String? = null
  private var sampleInterval: Int= DEFAULT_SAMPLE_INTERVAL

  fun init(sampleInterval: Int = DEFAULT_SAMPLE_INTERVAL) {
    if (sackThread == null) {
      sackThread = object : HandlerThread("BlockMonitor") {
        override fun onLooperPrepared() {
          stackHandler = Handler(sackThread!!.looper)
        }
      }
      sackThread?.start()
    }
    this.sampleInterval = sampleInterval
  }

  fun startDump() {
    if (stackHandler == null) {
      return
    }
    if (running.get()) {
      return
    }
    running.set(true)
    stackHandler?.removeCallbacks(runnable)
    stackHandler?.postDelayed(runnable, sampleInterval.toLong())
  }

  fun getThreadStackEntries(startTime: Long, endTime: Long): ArrayList<String> {
    val result = ArrayList<String>()
    synchronized(stackMap) {
      for (entryTime in stackMap.keys) {
        if (startTime < entryTime && entryTime < endTime) {
          result.add(
            (TIME_FORMATTER.format(entryTime)
                + SEPARATOR
                + SEPARATOR
                + stackMap[entryTime])
          )
        }
      }
    }
    return result
  }

  fun stopDump() {
    if (stackHandler == null) {
      return
    }
    if (!running.get()) {
      return
    }
    running.set(false)
    filterCache = null
    stackHandler?.removeCallbacks(runnable)
  }

  fun shutDown() {
    stopDump()
    sackThread?.quit()
  }


  private val runnable: Runnable = object : Runnable {
    override fun run() {
      dumpInfo()
      if (running.get()) {
        stackHandler?.postDelayed(this, sampleInterval.toLong())
      }
    }
  }

  private fun dumpInfo() {
    val stringBuilder = StringBuilder()
    val thread = Looper.getMainLooper().thread
    for (stackTraceElement in thread.stackTrace) {
      stringBuilder
        .append(stackTraceElement.toString())
        .append(SEPARATOR)
    }

    synchronized(stackMap) {
      if (stackMap.size == DEFAULT_MAX_ENTRY_COUNT) {
        stackMap.remove(stackMap.keys.iterator().next())
      }
      if (!shouldIgnore(stringBuilder)) {
        stackMap[System.currentTimeMillis()] = stringBuilder.toString()
      }
    }
  }

  /**
   * 过滤掉重复项
   *
   * @param builder
   * @return
   */
  private fun shouldIgnore(builder: StringBuilder): Boolean {
    if (TextUtils.equals(filterCache, builder.toString())) {
      return true
    }
    filterCache = builder.toString()
    return false
  }

  companion object {
    private const val TAG = "StackSampler"
    private const val DEFAULT_SAMPLE_INTERVAL = 300
    private const val DEFAULT_MAX_ENTRY_COUNT = 100
    private const val SEPARATOR = "\r\n"
    private val TIME_FORMATTER = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.CHINESE)
  }
}
