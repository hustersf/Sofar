package com.sofar.profiler.ui

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.sofar.profiler.MonitorCallback
import com.sofar.profiler.MonitorManager
import com.sofar.profiler.R
import com.sofar.profiler.activity.ActivityTimeInfo
import com.sofar.profiler.block.model.BlockInfo
import com.sofar.profiler.config.SdkConfig
import com.sofar.profiler.memory.model.MemoryInfo
import com.sofar.profiler.startup.StartupTracerV2
import com.sofar.profiler.startup.model.StartupInfo

class MonitorLayout(var activity: Activity) : FrameLayout(activity) {

  private lateinit var profilerIv: ImageView
  private lateinit var performanceLayout: ViewGroup
  private lateinit var businessLayout: ViewGroup

  private lateinit var fpsTv: TextView
  private lateinit var cpuTv: TextView
  private lateinit var threadTv: TextView
  private lateinit var fdTv: TextView
  private lateinit var memoryTv: TextView
  private lateinit var batteryTv: TextView
  private lateinit var trafficTv: TextView

  private lateinit var appStartTv: TextView
  private lateinit var activityStartTv: TextView

  private var config: SdkConfig = MonitorManager.config()

  private var callback: MonitorCallback = object : MonitorCallback {
    override fun onFrameRate(frameRate: Int) {
      super.onFrameRate(frameRate)
      fpsTv.text = "fps:$frameRate"
      setColor(frameRate < config.frameThreshold, fpsTv)
    }

    override fun onCpuRate(cpuRate: Float) {
      super.onCpuRate(cpuRate)
      cpuTv.text = "cpu:$cpuRate%"
      setColor(cpuRate > config.cpuThreshold, cpuTv)
    }

    override fun onThreadCount(count: Int) {
      super.onThreadCount(count)
      threadTv.text = "thread:$count"
      setColor(count > config.threadThreshold, threadTv)
    }

    override fun onMemoryInfo(info: MemoryInfo) {
      super.onMemoryInfo(info)
      memoryTv.text = "memory:$info"
      setColor(1.0f * info.pssTotalK / 1024 >= config.memoryThreshold, memoryTv)
    }

    override fun onFDCount(count: Int) {
      super.onFDCount(count)
      fdTv.text = "fd:$count"
      setColor(count > config.fdThreshold, fdTv)
    }

    override fun onBatteryInfo(temperature: Float) {
      super.onBatteryInfo(temperature)
      batteryTv.text = "battery:$temperature°C"
      setColor(temperature > config.batteryThreshold, batteryTv)
    }

    override fun onTrafficInfo(info: String) {
      super.onTrafficInfo(info)
      trafficTv.text = "network:\n$info"
    }

    override fun onAppStartInfo(info: StartupInfo) {
      super.onAppStartInfo(info)
      appStartTv.text = "app启动耗时(ms):\n$info"
      setColor(info.allCost > config.appStartThreshold, appStartTv)
    }

    override fun onActivityStartInfo(info: ActivityTimeInfo) {
      super.onActivityStartInfo(info)
      activityStartTv.text = "activity启动耗时(ms):\n$info"
      setColor(info.totalCost > config.activityThreshold, activityStartTv)
    }

    override fun onBlock(info: BlockInfo) {
      super.onBlock(info)
      Snackbar.make(profilerIv, "存在卡顿", Snackbar.LENGTH_SHORT).show()
    }
  }

  init {
    val view = LayoutInflater.from(activity).inflate(R.layout.monitor_view_layout, this, true)
    initView(view)
    initData()
  }

  private fun initView(view: View) {
    profilerIv = view.findViewById(R.id.profiler_iv)
    performanceLayout = view.findViewById(R.id.performance_layout)
    businessLayout = view.findViewById(R.id.business_layout)

    cpuTv = view.findViewById(R.id.cpu_tv)
    cpuTv.isVisible = config.cpuEnable
    fpsTv = view.findViewById(R.id.fps_tv)
    fpsTv.isVisible = config.frameEnable
    memoryTv = view.findViewById(R.id.memory_tv)
    memoryTv.isVisible = config.memoryEnable
    fdTv = view.findViewById(R.id.fd_tv)
    fdTv.isVisible = config.fdEnable
    threadTv = view.findViewById(R.id.thread_tv)
    threadTv.isVisible = config.threadEnable
    batteryTv = view.findViewById(R.id.battery_tv)
    batteryTv.isVisible = config.batteryEnable
    trafficTv = view.findViewById(R.id.traffic_tv)
    trafficTv.isVisible = config.trafficEnable

    appStartTv = view.findViewById(R.id.app_start_tv)
    appStartTv.isVisible = config.appStartEnable
    activityStartTv = view.findViewById(R.id.activity_start_tv)
    activityStartTv.isVisible = config.activityEnable

    profilerIv.setOnClickListener {
      var visible = performanceLayout.isVisible || businessLayout.isVisible
      if (needShowPerformanceLayout()) {
        performanceLayout.isVisible = !visible
      }
      if (needShowBusinessLayout()) {
        businessLayout.isVisible = !visible
      }
    }
    businessLayout.setOnClickListener {
      businessLayout.isVisible = !businessLayout.isVisible
    }
    performanceLayout.setOnClickListener {
      performanceLayout.isVisible = !performanceLayout.isVisible
    }
  }

  private fun initData() {
    if (appStartTv.isVisible) {
      StartupTracerV2.get().getStartupInfo()?.let {
        appStartTv.text = "app启动耗时(ms):\n${it.toString()}"
      }
    }
  }

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    MonitorManager.register(callback)
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    MonitorManager.unregister(callback)
  }

  private fun needShowPerformanceLayout(): Boolean {
    return config.cpuEnable || config.frameEnable || config.memoryEnable || config.fdEnable || config.threadEnable
        || config.batteryEnable || config.trafficEnable
  }

  private fun needShowBusinessLayout(): Boolean {
    return config.appStartEnable || config.activityEnable
  }

  private fun setColor(error: Boolean, textView: TextView) {
    if (error) {
      textView.setTextColor(ContextCompat.getColor(context, R.color.color_error))
    } else {
      textView.setTextColor(ContextCompat.getColor(context, R.color.color_panel_text))
    }
  }

}