package com.sofar.profiler.battery

import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.sofar.profiler.AbsMonitor
import com.sofar.profiler.MonitorManager
import com.sofar.profiler.MonitorType

class BatteryMonitor : AbsMonitor() {

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
    return MonitorType.BATTERY
  }

  private fun record() {
    val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    val batteryStatus: Intent? = appContext.registerReceiver(null, filter)
    batteryStatus?.let {
      var sb = StringBuilder()
      // 1. 获取电量百分比
      val level: Int = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
      val scale: Int = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
      val batteryPercent = level * 100.0f / scale
      sb.append("$batteryPercent%")

      // 2. 获取电池温度（单位为 0.1°C，需除以 10）
      val temperature: Int = it.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
      val tempCelsius = temperature / 10.0f
      sb.append(",$tempCelsius°C")

      // 3. 充电状态
      val status: Int = it.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
      val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
          status == BatteryManager.BATTERY_STATUS_FULL

      // 4. 充电方式（USB/交流/无线）
      val chargePlug: Int = it.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
      val usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
      val acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC

      // 5. 电压（单位：mV）
      val voltage: Int = it.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
      sb.append(",${voltage}mV")
      MonitorManager.batteryCallback(sb.toString())
    }
  }
}