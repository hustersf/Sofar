package com.sofar.profiler

import android.app.Application
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Process
import com.sofar.profiler.activity.ActivityLifecycleImpl
import com.sofar.profiler.activity.ActivityTimeInfo
import com.sofar.profiler.activity.ActivityTracer
import com.sofar.profiler.battery.BatteryMonitor
import com.sofar.profiler.block.core.BlockMonitorManager
import com.sofar.profiler.block.model.BlockInfo
import com.sofar.profiler.config.DEFAULT_COLLECT_INTERVAL_MILL
import com.sofar.profiler.config.SdkConfig
import com.sofar.profiler.config.loadSdkConfig
import com.sofar.profiler.cpu.CpuMonitor
import com.sofar.profiler.frame.FrameMonitor
import com.sofar.profiler.memory.FDMonitor
import com.sofar.profiler.memory.MemoryMonitor
import com.sofar.profiler.memory.ThreadMonitor
import com.sofar.profiler.memory.model.MemoryInfo
import com.sofar.profiler.startup.StartupTracerV2
import com.sofar.profiler.startup.model.StartupInfo
import com.sofar.profiler.traffic.TrafficMonitor
import com.sofar.profiler.ui.MonitorActivityLifecycleCallbacks
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set

object MonitorManager {
  private var map = HashMap<String, IMonitor>()

  private var init = false
  private var callbacks = LinkedHashSet<MonitorCallback>()
  private var collectInterval = DEFAULT_COLLECT_INTERVAL_MILL
  private lateinit var config: SdkConfig

  lateinit var appContext: Application
  var pid = 0
  var UIHandler = Handler(Looper.getMainLooper())
  lateinit var monitorHandler: Handler

  private var blockList = mutableListOf<BlockInfo>()

  @JvmStatic
  fun init(appContext: Application) {
    this.appContext = appContext
    pid = Process.myPid()
    val handlerThread = HandlerThread("profiler-monitor")
    handlerThread.start()
    monitorHandler = Handler(handlerThread.looper)
    initSdk()
  }

  private fun initSdk() {
    val sdkConfig = runBlocking {
      loadSdkConfig(appContext)
    }
    config = sdkConfig
    collectInterval = sdkConfig.collectInterval
    if (sdkConfig.cpuEnable) {
      map[key(CpuMonitor::class.java)] = CpuMonitor()
    }
    if (sdkConfig.frameEnable) {
      map[key(FrameMonitor::class.java)] = FrameMonitor()
    }
    if (sdkConfig.memoryEnable) {
      map[key(MemoryMonitor::class.java)] = MemoryMonitor()
    }
    if (sdkConfig.fdEnable) {
      map[key(FDMonitor::class.java)] = FDMonitor()
    }
    if (sdkConfig.threadEnable) {
      map[key(ThreadMonitor::class.java)] = ThreadMonitor()
    }
    if (sdkConfig.batteryEnable) {
      map[key(BatteryMonitor::class.java)] = BatteryMonitor()
    }
    if (sdkConfig.trafficEnable) {
      map[key(TrafficMonitor::class.java)] = TrafficMonitor()
    }
    var count = 0
    ActivityLifecycleImpl.get().init(appContext)
    if (sdkConfig.activityEnable) {
      ActivityTracer.get().init(appContext)
      count++
    }
    if (sdkConfig.blockEnable) {
      BlockMonitorManager.get().start(sdkConfig.blockTime)
      count++
    }
    if (sdkConfig.appStartEnable) {
      //放到最后,作为启动耗时统计的起始点
      StartupTracerV2.get().start(sdkConfig.hasSplashActivity)
      count++
    }
    if (map.isNotEmpty()) {
      start()
    }
    count += map.size
    if (count > 0) {
      appContext.registerActivityLifecycleCallbacks(MonitorActivityLifecycleCallbacks())
    }
    init = true
  }


  private fun key(modelClass: Class<*>): String {
    return "app.profiler.${modelClass.canonicalName}"
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

  fun memoryCallback(info: MemoryInfo) {
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

  fun batteryCallback(temperature: Float) {
    UIHandler.post {
      for (callback in callbacks) {
        callback.onBatteryInfo(temperature)
      }
    }
  }

  fun trafficCallback(info: String) {
    UIHandler.post {
      for (callback in callbacks) {
        callback.onTrafficInfo(info)
      }
    }
  }

  fun appStartCallback(info: StartupInfo) {
    UIHandler.post {
      for (callback in callbacks) {
        callback.onAppStartInfo(info)
      }
    }
  }


  fun activityStartCallback(info: ActivityTimeInfo) {
    UIHandler.post {
      for (callback in callbacks) {
        callback.onActivityStartInfo(info)
      }
    }
  }

  fun blockCallback(info: BlockInfo) {
    UIHandler.post {
      blockList.add(0, info)
      for (callback in callbacks) {
        callback.onBlock(info)
      }
    }
  }

  fun blockList(): List<BlockInfo> {
    return blockList
  }

  fun collectInterval(): Long {
    return collectInterval.toLong()
  }

  fun config(): SdkConfig {
    return config
  }

}