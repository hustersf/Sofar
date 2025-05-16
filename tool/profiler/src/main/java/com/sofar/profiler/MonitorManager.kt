package com.sofar.profiler

import android.app.Application
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Process
import com.sofar.profiler.activity.ActivityTracer
import com.sofar.profiler.cpu.CpuMonitor
import com.sofar.profiler.frame.FrameMonitor
import com.sofar.profiler.memory.FDMonitor
import com.sofar.profiler.memory.MemoryMonitor
import com.sofar.profiler.memory.ThreadMonitor
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set

object MonitorManager {

  lateinit var appContext: Application
  var pid = 0

  var UIHandler = Handler(Looper.getMainLooper())
  lateinit var monitorHandler: Handler

  var map = HashMap<String, IMonitor>()
  var callbacks = ArrayList<MonitorCallback>()

  @JvmStatic
  fun init(appContext: Application) {
    this.appContext = appContext
    pid = Process.myPid()
    val handlerThread = HandlerThread("profiler-monitor")
    handlerThread.start()
    monitorHandler = Handler(handlerThread.looper)

    ActivityTracer.get().init(appContext)
  }

  private fun key(modelClass: Class<*>): String {
    return "app.profiler.${modelClass.canonicalName}"
  }

  @JvmStatic
  fun addAll() {
    map[key(FrameMonitor::class.java)] = FrameMonitor()
    map[key(CpuMonitor::class.java)] = CpuMonitor()
    map[key(MemoryMonitor::class.java)] = MemoryMonitor()
    map[key(ThreadMonitor::class.java)] = ThreadMonitor()
    map[key(FDMonitor::class.java)] = FDMonitor()
  }

  @JvmStatic
  fun addMonitor(monitor: IMonitor) {
    map[key(monitor::class.java)] = monitor
  }

  @JvmStatic
  fun <T : IMonitor> getMonitor(modelClass: Class<T>): T {
    var key = key(modelClass)
    var monitor = map[key]
    return if (modelClass.isInstance(monitor)) {
      monitor as T
    } else {
      modelClass.newInstance()
    }
  }


  @JvmStatic
  fun start() {
    map.forEach {
      it.value.start()
    }
  }

  @JvmStatic
  fun stop() {
    map.forEach {
      it.value.stop()
    }
  }

  @JvmStatic
  fun register(callback: MonitorCallback) {
    callbacks.add(callback)
  }

  @JvmStatic
  fun unregister(callback: MonitorCallback) {
    callbacks.remove(callback)
  }

  fun frameCallback(frameRate: Int) {
    UIHandler.post {
      for (callback in callbacks) {
        callback.onFrameRate(frameRate)
      }
    }
  }

  fun cpuCallback(cpuRate: Float) {
    UIHandler.post {
      for (callback in callbacks) {
        callback.onCpuRate(cpuRate)
      }
    }
  }

  fun threadCallback(count: Int) {
    UIHandler.post {
      for (callback in callbacks) {
        callback.onThreadCount(count)
      }
    }
  }

  fun memoryCallback(info: String) {
    UIHandler.post {
      for (callback in callbacks) {
        callback.onMemoryInfo(info)
      }
    }
  }

  fun fdCallback(count: Int) {
    UIHandler.post {
      for (callback in callbacks) {
        callback.onFDCount(count)
      }
    }
  }

}