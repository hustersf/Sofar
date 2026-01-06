package com.sofar.profiler

import androidx.annotation.MainThread
import com.sofar.profiler.activity.ActivityTimeInfo
import com.sofar.profiler.block.model.BlockInfo
import com.sofar.profiler.memory.model.MemoryInfo
import com.sofar.profiler.startup.model.StartupInfo

interface MonitorCallback {

  @MainThread
  fun onFrameRate(frameRate: Int) {
  }

  @MainThread
  fun onCpuRate(cpuRate: Float) {
  }

  @MainThread
  fun onThreadCount(count: Int) {
  }

  @MainThread
  fun onMemoryInfo(info: MemoryInfo) {
  }

  @MainThread
  fun onFDCount(count: Int) {
  }

  @MainThread
  fun onBatteryInfo(temperature: Float) {
  }

  @MainThread
  fun onTrafficInfo(info: String) {
  }

  @MainThread
  fun onAppStartInfo(info: StartupInfo) {
  }

  @MainThread
  fun onActivityStartInfo(info: ActivityTimeInfo) {
  }

  @MainThread
  fun onBlock(info: BlockInfo) {
  }
}
