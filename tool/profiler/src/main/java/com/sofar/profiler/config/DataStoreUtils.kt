package com.sofar.profiler.config

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sofar.profiler.getProcessName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "profile_settings")

val APP_START_KEY = booleanPreferencesKey("app_start_switch")
val APP_START_THRESHOLD_KEY = intPreferencesKey("app_start_threshold")
val APP_START_SPLASH_KEY = booleanPreferencesKey("app_start_splash")
val ACTIVITY_KEY = booleanPreferencesKey("activity_start_switch")
val ACTIVITY_THRESHOLD_KEY = intPreferencesKey("activity_start_threshold")
val COLLECT_INTERVAL_KEY = intPreferencesKey("collect_interval")
val CPU_KEY = booleanPreferencesKey("cpu_switch")
val CPU_THRESHOLD_KEY = intPreferencesKey("cpu_threshold")
val FRAME_KEY = booleanPreferencesKey("frame_switch")
val FRAME_THRESHOLD_KEY = intPreferencesKey("frame_threshold")
val MEMORY_KEY = booleanPreferencesKey("memory_switch")
val MEMORY_THRESHOLD_KEY = intPreferencesKey("memory_threshold")
val FD_KEY = booleanPreferencesKey("fd_switch")
val FD_THRESHOLD_KEY = intPreferencesKey("fd_threshold")
val THREAD_KEY = booleanPreferencesKey("thread_switch")
val THREAD_THRESHOLD_KEY = intPreferencesKey("thread_threshold")
val BATTERY_KEY = booleanPreferencesKey("battery_switch")
val BATTERY_THRESHOLD_KEY = intPreferencesKey("battery_threshold")
val TRAFFIC_KEY = booleanPreferencesKey("traffic_switch")
val BLOCK_KEY = booleanPreferencesKey("block_switch")
val BLOCK_TIME_KEY = intPreferencesKey("block_time")
val BLOCK_PACKAGE_KEY = stringPreferencesKey("block_package")

const val DEFAULT_COLLECT_INTERVAL_MILL = 1000
const val DEFAULT_BLOCK_THRESHOLD_MILL = 200

const val DEFAULT_APP_START_THRESHOLD = 3000
const val DEFAULT_ACTIVITY_START_THRESHOLD = 1000
const val DEFAULT_CPU_THRESHOLD = 60
const val DEFAULT_FRAME_THRESHOLD = 45
const val DEFAULT_MEMORY_THRESHOLD = 400
const val DEFAULT_FD_THRESHOLD = 1024
const val DEFAULT_THREAD_THRESHOLD = 150
const val DEFAULT_BATTERY_THRESHOLD = 40

// 定义一个顶层函数用于存储用户名称
suspend fun saveProfilerSwitch(context: Context, key: Preferences.Key<Boolean>, switch: Boolean) {
  context.dataStore.edit { preferences ->
    preferences[key] = switch
  }
}

// 定义一个顶层函数用于读取用户名称，返回 Flow 以便实时监听数据变化
fun profilerSwitch(context: Context, key: Preferences.Key<Boolean>): Flow<Boolean> {
  return context.dataStore.data
    .catch { exception ->
      // 捕获 IOException，防止由于数据损坏导致崩溃
      if (exception is IOException) {
        emit(emptyPreferences())
      } else {
        throw exception
      }
    }
    .map { preferences ->
      // 读取出用户名称，如果不存在则返回默认值
      preferences[key] ?: false
    }
}

suspend fun loadSdkConfig(context: Context): SdkConfig {
  val prefs = context.dataStore.data.first()
  return SdkConfig(
    appStartEnable = prefs[APP_START_KEY] ?: false,
    appStartThreshold = prefs[APP_START_THRESHOLD_KEY] ?: DEFAULT_APP_START_THRESHOLD,
    hasSplashActivity = prefs[APP_START_SPLASH_KEY] ?: true,
    activityEnable = prefs[ACTIVITY_KEY] ?: false,
    activityThreshold = prefs[ACTIVITY_THRESHOLD_KEY] ?: DEFAULT_ACTIVITY_START_THRESHOLD,
    collectInterval = prefs[COLLECT_INTERVAL_KEY] ?: DEFAULT_COLLECT_INTERVAL_MILL,
    cpuEnable = prefs[CPU_KEY] ?: false,
    cpuThreshold = prefs[CPU_THRESHOLD_KEY] ?: DEFAULT_CPU_THRESHOLD,
    frameEnable = prefs[FRAME_KEY] ?: false,
    frameThreshold = prefs[FRAME_THRESHOLD_KEY] ?: DEFAULT_FRAME_THRESHOLD,
    memoryEnable = prefs[MEMORY_KEY] ?: false,
    memoryThreshold = prefs[MEMORY_THRESHOLD_KEY] ?: DEFAULT_MEMORY_THRESHOLD,
    fdEnable = prefs[FD_KEY] ?: false,
    fdThreshold = prefs[FD_THRESHOLD_KEY] ?: DEFAULT_FD_THRESHOLD,
    threadEnable = prefs[THREAD_KEY] ?: false,
    threadThreshold = prefs[THREAD_THRESHOLD_KEY] ?: DEFAULT_THREAD_THRESHOLD,
    batteryEnable = prefs[BATTERY_KEY] ?: false,
    batteryThreshold = prefs[BATTERY_THRESHOLD_KEY] ?: DEFAULT_BATTERY_THRESHOLD,
    trafficEnable = prefs[TRAFFIC_KEY] ?: false,
    blockEnable = prefs[BLOCK_KEY] ?: false,
    blockTime = prefs[BLOCK_TIME_KEY] ?: DEFAULT_BLOCK_THRESHOLD_MILL,
    blockPackage = prefs[BLOCK_PACKAGE_KEY] ?: getProcessName()
  )
}