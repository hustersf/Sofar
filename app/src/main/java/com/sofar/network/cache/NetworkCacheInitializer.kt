package com.sofar.network.cache

import android.content.Context
import com.sofar.network.cache.monitor.DefaultSdkLogger
import com.sofar.network.cache.monitor.ICacheMonitor
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.io.File

/**
 * 全局网络缓存初始化与日志流分发器
 */
object NetworkCacheInitializer {

  // 供 Activity 监听的缓存日志管道
  private val _logFlow = MutableSharedFlow<String>(extraBufferCapacity = 100)
  val logFlow: SharedFlow<String> = _logFlow

  // 供 Application 全局调用的一键初始化
  fun init(context: Context) {
    NetworkCache.init(
      NetworkCache.Builder(
        cacheDir = File(context.externalCacheDir, "network_cache")
      ).setLogger(
        DefaultSdkLogger(true)
      ).setMonitor(object : ICacheMonitor {
        // 只管往管道发射日志，不持有任何 Activity 引用，彻底杜绝内存泄漏和去重覆盖失效问题
        private fun emit(msg: String) = _logFlow.tryEmit(msg)

        override fun onCacheHit(urlPath: String) {
          emit("cache hit: $urlPath")
        }

        override fun onCacheMiss(urlPath: String) {
          emit("cache miss: $urlPath")
        }

        override fun onCacheExpired(urlPath: String) {
          emit("cache expired: $urlPath")
        }

        override fun onCacheReadFailed(urlPath: String, t: Throwable) {
          emit("cache read failed: ${t.message}")
        }

        override fun onCacheWriteFailed(urlPath: String, t: Throwable) {
          emit("cache write failed: ${t.message}")
        }

        override fun onNetworkSuccess(urlPath: String, costMs: Long) {
          emit("network success: $urlPath (${costMs}ms)")
        }

        override fun onNetworkFailed(urlPath: String, t: Throwable, costMs: Long) {
          emit("network failed: ${t.message}")
        }
      }).build()
    )
  }
}
