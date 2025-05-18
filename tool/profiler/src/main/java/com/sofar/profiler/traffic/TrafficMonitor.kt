package com.sofar.profiler.traffic

import android.net.TrafficStats
import com.sofar.profiler.AbsMonitor
import com.sofar.profiler.MonitorManager
import com.sofar.profiler.MonitorType

class TrafficMonitor : AbsMonitor() {

  private val uid = android.os.Process.myUid()
  // 总接收流量（字节）
  private val initRxBytes = TrafficStats.getUidRxBytes(uid)
  // 总发送流量（字节）
  private val initTxBytes = TrafficStats.getUidTxBytes(uid)

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
    return MonitorType.TRAFFIC
  }

  private fun record() {
    var sb = StringBuilder()
    val rxBytes = TrafficStats.getUidRxBytes(uid)
    sb.append("数据下载:${rxBytes-initRxBytes}B")
    val txBytes = TrafficStats.getUidTxBytes(uid)
    sb.append(",数据上传:${txBytes-initTxBytes}B")
    MonitorManager.trafficCallback(sb.toString())
  }
}