package com.sofar.profiler.memory

import android.util.Log
import com.sofar.profiler.AbsMonitor
import com.sofar.profiler.MonitorManager
import com.sofar.profiler.MonitorType
import java.io.File
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
    var taskDir = File("/proc/self/task")
    count = taskDir.listFiles().size
    if (count > warningCount) {
      Log.d(tag, getInfo())
    }
  }


  fun getInfo(): String {
    val taskDir = File("/proc/self/task")
    val sb = StringBuilder()
    if (taskDir.exists() && taskDir.isDirectory()) {
      taskDir.listFiles()?.forEach { threadDir ->
        val tid = threadDir.name.toIntOrNull()
        if (tid != null) {
          // 获取线程名称
          val commFile = File(threadDir, "comm")
          if (commFile.exists()) {
            val name = commFile.readText().trim()
            sb.append(name).append("\n")
          }
        }
      }
    }
    return sb.toString()
  }

  fun getJavaThreadInfo(): String {
    val threadSet: Set<Thread> = Thread.getAllStackTraces().keys
    val sb = StringBuilder()
    for (t in threadSet) {
      sb.append(t.name).append("\n")
    }
    Log.d(tag, "thread count={${threadSet.size}}")
    return sb.toString()
  }

}