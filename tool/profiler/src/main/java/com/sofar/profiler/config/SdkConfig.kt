package com.sofar.profiler.config

data class SdkConfig(
  val appStartEnable: Boolean,
  val appStartThreshold: Int,
  val hasSplashActivity: Boolean,
  val activityEnable: Boolean,
  val activityThreshold: Int,
  val collectInterval: Int,
  val cpuEnable: Boolean,
  val cpuThreshold: Int,
  val frameEnable: Boolean,
  val frameThreshold: Int,
  val memoryEnable: Boolean,
  val memoryThreshold: Int,
  val fdEnable: Boolean,
  val fdThreshold: Int,
  val threadEnable: Boolean,
  val threadThreshold: Int,
  val batteryEnable: Boolean,
  val batteryThreshold: Int,
  val trafficEnable: Boolean,
  val blockEnable: Boolean,
  val blockTime: Int,
  val blockPackage: String
)