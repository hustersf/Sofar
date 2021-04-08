package com.sofar.profiler

interface IMonitor {

  fun start()

  fun stop()

  fun type(): MonitorType

  fun pollInterval(): Long

}