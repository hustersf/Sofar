package com.sofar.profiler.ui.config

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.SwitchCompat
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.sofar.profiler.MonitorManager.appContext
import com.sofar.profiler.R
import com.sofar.profiler.config.ACTIVITY_KEY
import com.sofar.profiler.config.ACTIVITY_THRESHOLD_KEY
import com.sofar.profiler.config.APP_START_KEY
import com.sofar.profiler.config.APP_START_SPLASH_KEY
import com.sofar.profiler.config.APP_START_THRESHOLD_KEY
import com.sofar.profiler.config.BATTERY_KEY
import com.sofar.profiler.config.BATTERY_THRESHOLD_KEY
import com.sofar.profiler.config.BLOCK_KEY
import com.sofar.profiler.config.BLOCK_PACKAGE_KEY
import com.sofar.profiler.config.BLOCK_TIME_KEY
import com.sofar.profiler.config.COLLECT_INTERVAL_KEY
import com.sofar.profiler.config.CPU_KEY
import com.sofar.profiler.config.CPU_THRESHOLD_KEY
import com.sofar.profiler.config.DEFAULT_ACTIVITY_START_THRESHOLD
import com.sofar.profiler.config.DEFAULT_APP_START_THRESHOLD
import com.sofar.profiler.config.DEFAULT_BATTERY_THRESHOLD
import com.sofar.profiler.config.DEFAULT_BLOCK_THRESHOLD_MILL
import com.sofar.profiler.config.DEFAULT_CPU_THRESHOLD
import com.sofar.profiler.config.DEFAULT_FD_THRESHOLD
import com.sofar.profiler.config.DEFAULT_FRAME_THRESHOLD
import com.sofar.profiler.config.DEFAULT_MEMORY_THRESHOLD
import com.sofar.profiler.config.DEFAULT_THREAD_THRESHOLD
import com.sofar.profiler.config.FD_KEY
import com.sofar.profiler.config.FD_THRESHOLD_KEY
import com.sofar.profiler.config.FRAME_KEY
import com.sofar.profiler.config.FRAME_THRESHOLD_KEY
import com.sofar.profiler.config.MEMORY_KEY
import com.sofar.profiler.config.MEMORY_THRESHOLD_KEY
import com.sofar.profiler.config.THREAD_KEY
import com.sofar.profiler.config.THREAD_THRESHOLD_KEY
import com.sofar.profiler.config.TRAFFIC_KEY
import com.sofar.profiler.config.dataStore
import com.sofar.profiler.config.loadSdkConfig
import com.sofar.profiler.config.saveProfilerSwitch
import kotlinx.coroutines.launch

class MonitorConfigFragment : Fragment() {

  private lateinit var restoreBtn: Button

  private lateinit var businessBtn: Button
  private lateinit var appStartSwitch: SwitchCompat
  private lateinit var appSplashSwitch: SwitchCompat
  private lateinit var activitySwitch: SwitchCompat
  private lateinit var appStartEt: EditText
  private lateinit var activityEt: EditText

  private lateinit var commonBtn: Button
  private lateinit var intervalEt: EditText
  private lateinit var cpuSwitch: SwitchCompat
  private lateinit var cpuEt: EditText
  private lateinit var frameSwitch: SwitchCompat
  private lateinit var frameEt: EditText
  private lateinit var memorySwitch: SwitchCompat
  private lateinit var memoryEt: EditText
  private lateinit var fdSwitch: SwitchCompat
  private lateinit var fdEt: EditText
  private lateinit var threadSwitch: SwitchCompat
  private lateinit var threadEt: EditText
  private lateinit var batterySwitch: SwitchCompat
  private lateinit var batteryEt: EditText
  private lateinit var trafficSwitch: SwitchCompat
  private lateinit var blockBtn: Button
  private lateinit var blockSwitch: SwitchCompat
  private lateinit var blockTimeEt: EditText
  private lateinit var blockPackageEt: EditText

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.monitor_config_fragment, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initView(view)
    initData()
  }

  fun initView(view: View) {
    restoreBtn = view.findViewById(R.id.restore_btn)
    restoreBtn.setOnClickListener {
      restoreData()
    }

    businessBtn = view.findViewById(R.id.business_save_btn)
    businessBtn.setOnClickListener {
      saveBusinessData()
    }
    appStartSwitch = view.findViewById(R.id.app_start_switch)
    setSwitchCheckedListener(appStartSwitch, APP_START_KEY)
    appStartEt = view.findViewById(R.id.app_start_et)
    appSplashSwitch = view.findViewById(R.id.app_splash_switch)
    setSwitchCheckedListener(appSplashSwitch, APP_START_SPLASH_KEY)

    activitySwitch = view.findViewById(R.id.activity_switch)
    setSwitchCheckedListener(activitySwitch, ACTIVITY_KEY)
    activityEt = view.findViewById(R.id.activity_et)

    commonBtn = view.findViewById(R.id.common_save_btn)
    commonBtn.setOnClickListener {
      saveCommonData()
    }
    intervalEt = view.findViewById(R.id.collect_interval_et)

    cpuSwitch = view.findViewById(R.id.cpu_switch)
    setSwitchCheckedListener(cpuSwitch, CPU_KEY)
    cpuEt = view.findViewById(R.id.cpu_et)

    frameSwitch = view.findViewById(R.id.frame_switch)
    setSwitchCheckedListener(frameSwitch, FRAME_KEY)
    frameEt = view.findViewById(R.id.frame_et)

    memorySwitch = view.findViewById(R.id.memory_switch)
    setSwitchCheckedListener(memorySwitch, MEMORY_KEY)
    memoryEt = view.findViewById(R.id.memory_et)

    fdSwitch = view.findViewById(R.id.fd_switch)
    setSwitchCheckedListener(fdSwitch, FD_KEY)
    fdEt = view.findViewById(R.id.fd_et)

    threadSwitch = view.findViewById(R.id.thread_switch)
    setSwitchCheckedListener(threadSwitch, THREAD_KEY)
    threadEt = view.findViewById(R.id.thread_et)

    batterySwitch = view.findViewById(R.id.battery_switch)
    setSwitchCheckedListener(batterySwitch, BATTERY_KEY)
    batteryEt = view.findViewById(R.id.battery_et)

    trafficSwitch = view.findViewById(R.id.traffic_switch)
    setSwitchCheckedListener(trafficSwitch, TRAFFIC_KEY)

    blockBtn = view.findViewById(R.id.block_save_btn)
    blockBtn.setOnClickListener {
      saveBlockData()
    }
    blockSwitch = view.findViewById(R.id.block_switch)
    setSwitchCheckedListener(blockSwitch, BLOCK_KEY)
    blockTimeEt = view.findViewById(R.id.block_time_et)
    blockPackageEt = view.findViewById(R.id.block_package_et)
  }

  fun initData() {
    lifecycleScope.launch {
      var sdkConfig = loadSdkConfig(appContext)
      appStartSwitch.isChecked = sdkConfig.appStartEnable
      appSplashSwitch.isChecked = sdkConfig.hasSplashActivity
      appStartEt.setText(sdkConfig.appStartThreshold.toString())

      activitySwitch.isChecked = sdkConfig.activityEnable
      activityEt.setText(sdkConfig.activityThreshold.toString())

      intervalEt.setText(sdkConfig.collectInterval.toString())

      cpuSwitch.isChecked = sdkConfig.cpuEnable
      cpuEt.setText(sdkConfig.cpuThreshold.toString())

      frameSwitch.isChecked = sdkConfig.frameEnable
      frameEt.setText(sdkConfig.frameThreshold.toString())

      memorySwitch.isChecked = sdkConfig.memoryEnable
      memoryEt.setText(sdkConfig.memoryThreshold.toString())

      fdSwitch.isChecked = sdkConfig.fdEnable
      fdEt.setText(sdkConfig.fdThreshold.toString())

      threadSwitch.isChecked = sdkConfig.threadEnable
      threadEt.setText(sdkConfig.threadThreshold.toString())

      batterySwitch.isChecked = sdkConfig.batteryEnable
      batteryEt.setText(sdkConfig.batteryThreshold.toString())

      trafficSwitch.isChecked = sdkConfig.trafficEnable

      blockSwitch.isChecked = sdkConfig.blockEnable
      blockTimeEt.setText(sdkConfig.blockTime.toString())
      blockPackageEt.setText(sdkConfig.blockPackage)
    }
  }

  fun setSwitchCheckedListener(switchCompat: SwitchCompat, key: Preferences.Key<Boolean>) {
    switchCompat.setOnCheckedChangeListener { buttonView, isChecked ->
      saveConfig(key, isChecked)
    }
  }

  fun saveConfig(key: Preferences.Key<Boolean>, switch: Boolean) {
    lifecycleScope.launch {
      saveProfilerSwitch(requireActivity(), key, switch)
    }
  }

  private fun saveBusinessData() {
    lifecycleScope.launch {
      requireActivity().dataStore.edit { preferences ->
        preferences[APP_START_THRESHOLD_KEY] = appStartEt.text.toString().toInt()
        preferences[ACTIVITY_THRESHOLD_KEY] = activityEt.text.toString().toInt()
      }
    }
  }

  private fun saveCommonData() {
    lifecycleScope.launch {
      requireActivity().dataStore.edit { preferences ->
        preferences[COLLECT_INTERVAL_KEY] = intervalEt.text.toString().toInt()
        preferences[CPU_THRESHOLD_KEY] = cpuEt.text.toString().toInt()
        preferences[FRAME_THRESHOLD_KEY] = frameEt.text.toString().toInt()
        preferences[MEMORY_THRESHOLD_KEY] = memoryEt.text.toString().toInt()
        preferences[FD_THRESHOLD_KEY] = fdEt.text.toString().toInt()
        preferences[THREAD_THRESHOLD_KEY] = threadEt.text.toString().toInt()
        preferences[BATTERY_THRESHOLD_KEY] = batteryEt.text.toString().toInt()
      }
    }
  }

  private fun saveBlockData() {
    lifecycleScope.launch {
      requireActivity().dataStore.edit { preferences ->
        preferences[BLOCK_TIME_KEY] = blockTimeEt.text.toString().toInt()
        preferences[BLOCK_PACKAGE_KEY] = blockPackageEt.text.toString()
      }
    }
  }

  private fun restoreData() {
    lifecycleScope.launch {
      requireActivity().dataStore.edit { preferences ->
        preferences[APP_START_THRESHOLD_KEY] = DEFAULT_APP_START_THRESHOLD
        preferences[ACTIVITY_THRESHOLD_KEY] = DEFAULT_ACTIVITY_START_THRESHOLD

        preferences[CPU_THRESHOLD_KEY] = DEFAULT_CPU_THRESHOLD
        preferences[FRAME_THRESHOLD_KEY] = DEFAULT_FRAME_THRESHOLD
        preferences[MEMORY_THRESHOLD_KEY] = DEFAULT_MEMORY_THRESHOLD
        preferences[FD_THRESHOLD_KEY] = DEFAULT_FD_THRESHOLD
        preferences[THREAD_THRESHOLD_KEY] = DEFAULT_THREAD_THRESHOLD
        preferences[BATTERY_THRESHOLD_KEY] = DEFAULT_BATTERY_THRESHOLD

        preferences[BLOCK_TIME_KEY] = DEFAULT_BLOCK_THRESHOLD_MILL
      }

      initData()
    }
  }
}
