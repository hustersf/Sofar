package com.sofar.network.cache.monitor

/**
 * 缓存运行过程中的关键事件监听器。
 *
 * 用于命中率统计、性能监控、异常上报等场景。
 */
interface ICacheMonitor {

  /**
   * 缓存命中。
   */
  fun onCacheHit(urlPath: String)

  /**
   * 缓存未命中。
   */
  fun onCacheMiss(urlPath: String)

  /**
   * 缓存存在，但已过期。
   */
  fun onCacheExpired(urlPath: String)

  /**
   * 缓存读取失败。
   */
  fun onCacheReadFailed(urlPath: String, throwable: Throwable)

  /**
   * 缓存写入失败。
   */
  fun onCacheWriteFailed(urlPath: String, throwable: Throwable)

  /**
   * 网络请求成功。
   *
   * @param costMs 网络耗时（毫秒）
   */
  fun onNetworkSuccess(urlPath: String, costMs: Long)

  /**
   * 网络请求失败。
   *
   * @param costMs 网络耗时（毫秒）
   */
  fun onNetworkFailed(urlPath: String, throwable: Throwable, costMs: Long)
}
