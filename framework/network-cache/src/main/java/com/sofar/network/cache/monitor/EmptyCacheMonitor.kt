package com.sofar.network.cache.monitor

/**
 * 默认空实现。
 *
 * 当业务未配置 Monitor 时使用，
 * 避免在业务代码中频繁进行 null 判断。
 */
class EmptyCacheMonitor : ICacheMonitor {

  override fun onCacheHit(urlPath: String) = Unit

  override fun onCacheMiss(urlPath: String) = Unit

  override fun onCacheExpired(urlPath: String) = Unit

  override fun onCacheReadFailed(
    urlPath: String,
    throwable: Throwable
  ) = Unit

  override fun onCacheWriteFailed(
    urlPath: String,
    throwable: Throwable
  ) = Unit

  override fun onNetworkSuccess(
    urlPath: String,
    costMs: Long
  ) = Unit

  override fun onNetworkFailed(
    urlPath: String,
    throwable: Throwable,
    costMs: Long
  ) = Unit
}
