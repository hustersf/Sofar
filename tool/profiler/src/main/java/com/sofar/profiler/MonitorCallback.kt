package com.sofar.profiler

import androidx.annotation.MainThread

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
  fun onMemoryInfo(info: String) {
  }

  @MainThread
  fun onFDCount(count: Int) {
  }

  @MainThread
  fun onBatteryInfo(info: String) {
  }

  @MainThread
  fun onTrafficInfo(info: String) {
  }
}