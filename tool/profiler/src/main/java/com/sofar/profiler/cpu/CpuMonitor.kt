package com.sofar.profiler.cpu

import android.os.Build
import android.os.Build.VERSION
import android.text.TextUtils
import android.util.Log
import com.sofar.profiler.AbsMonitor
import com.sofar.profiler.MonitorManager
import com.sofar.profiler.MonitorType
import com.sofar.profiler.formatNumber
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class CpuMonitor : AbsMonitor() {

  private var tag = "CpuMonitor"
  private var cpuRate = 0f

  private var lastCpuTime: Long? = null
  private var lastAppCpuTime: Long? = null

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
    return MonitorType.CPU
  }

  private fun record() {
    try {
      cpuRate = if (VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        getCpuDataForO()
      } else {
        getCpuData()
      }
      cpuRate = formatNumber(cpuRate).toFloat()
      Log.d(tag, "cpu使用率=$cpuRate%")
      MonitorManager.cpuCallback(cpuRate)
    } catch (e: Exception) {
      Log.d(tag, e.toString())
    }
  }

  fun getCpuRate(): Float {
    return cpuRate
  }

  private fun getCpuDataForO(): Float {
    var process = Runtime.getRuntime().exec("top -n 1")
    val reader = BufferedReader(InputStreamReader(process.inputStream))
    var line: String
    var cpuIndex = -1
    while (reader.readLine().also { line = it } != null) {
      line = line.trim { it <= ' ' }
      if (TextUtils.isEmpty(line)) {
        continue
      }

      // Log.d(tag, "line=$line")
      val tempIndex = getCPUIndex(line)
      if (tempIndex != -1) {
        cpuIndex = tempIndex
        continue
      }

      if (line.startsWith(pid)) {
        if (cpuIndex == -1) {
          continue
        }
        val param = line.split(Regex("\\s+")).toTypedArray()
        if (param.size <= cpuIndex) {
          continue
        }
        var cpu = param[cpuIndex]
        if (cpu.endsWith("%")) {
          cpu = cpu.substring(0, cpu.lastIndexOf("%"))
        }
        return cpu.toFloat() / Runtime.getRuntime().availableProcessors()
      }
    }

    return 0f
  }

  private fun getCpuData(): Float {
    var value = 0f
    var procStats = File("/proc/stat").readLines()[0].split(Regex("\\s+"))
    var appStats = File("/proc/$pid/stat").readLines()[0].split(Regex("\\s+"))

    var cpuTime = procStats[2].toLong() + procStats[3].toLong() + procStats[4].toLong() +
        procStats[5].toLong() + procStats[6].toLong() + procStats[7].toLong() + procStats[8].toLong()
    var appTime = appStats[13].toLong() + appStats[14].toLong()
    if (lastCpuTime == null && lastAppCpuTime == null) {
      lastCpuTime = cpuTime
      lastAppCpuTime = appTime
      return value
    }
    value = 100f * (appTime - lastAppCpuTime!!) / (cpuTime - lastCpuTime!!)
    lastCpuTime = cpuTime
    lastAppCpuTime = appTime

    return value
  }

  private fun getCPUIndex(line: String): Int {
    if (line.contains("CPU")) {
      val titles = line.split(Regex("\\s+")).toTypedArray()
      for (i in titles.indices) {
        if (titles[i].contains("CPU")) {
          return i
        }
      }
    }
    return -1
  }

}