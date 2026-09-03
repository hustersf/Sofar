package com.sofar.network.cache

import android.util.Log
import com.sofar.network.cache.key.CacheKeyTransformer
import com.sofar.network.cache.monitor.DefaultSdkLogger
import com.sofar.network.cache.monitor.EmptyCacheMonitor
import com.sofar.network.cache.monitor.ICacheMonitor
import com.sofar.network.cache.monitor.ISdkLogger
import com.sofar.network.cache.policy.LoadPolicy
import com.sofar.network.cache.predicate.CachePredicate
import com.sofar.network.cache.storage.CacheStorageManager
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

/**
 * SDK 核心入口类
 * 职责：仅承接、托管业务方传入的全局配置项。
 */
class NetworkCache private constructor() {

  class Config internal constructor(
    val cacheDir: File,                             // 磁盘缓存根目录
    val maxMemorySize: Int,                         // 内存 LruCache 上限（字节）
    val maxDiskSize: Long,                          // 磁盘 LruCache 上限（字节）
    val ttl: Long,                                  // 接口未配置 ttl 时使用的默认 TTL 值
    val ttlUnit: TimeUnit,                          // 接口未配置 ttl 时使用的默认 TTL 单位
    val loadPolicy: LoadPolicy,                     // 接口未配置读取策略时使用的默认策略
    val logger: ISdkLogger,                         // 调试日志处理器
    val monitor: ICacheMonitor,                     // 关键节点监控器
    val deduplicateResponse: Boolean,               // 过滤连续相同响应，依赖 DTO equals()
    val cachePredicate: CachePredicate?,            // 缓存写入判断器，null 表示默认全部缓存
    val cacheKeyTransformer: CacheKeyTransformer?,  // 缓存 Key 转换器，返回 null 或空白字符串时，沿用 SDK 默认规则。
    val enableDiskEncryption: Boolean,              // 磁盘缓存 AES-GCM 加密开关，默认开启
  ) {
    val ttlMillis: Long = ttlUnit.toMillis(ttl)
  }

  class Builder(
    private val cacheDir: File,
  ) {
    private var maxMemorySize: Int = calculateDefaultMemorySize()
    private var maxDiskSize: Long = DEFAULT_DISK_CACHE_SIZE
    private var ttl: Long = DEFAULT_TTL
    private var ttlUnit: TimeUnit = DEFAULT_TTL_UNIT
    private var loadPolicy: LoadPolicy = DEFAULT_LOAD_POLICY
    private var logger: ISdkLogger = DefaultSdkLogger(isDebug = false)
    private var monitor: ICacheMonitor = EmptyCacheMonitor()
    private var deduplicateResponse: Boolean = false
    private var cachePredicate: CachePredicate? = null
    private var cacheKeyTransformer: CacheKeyTransformer? = null
    private var enableDiskEncryption: Boolean = true

    fun setMaxMemorySize(sizeInBytes: Int) = apply { this.maxMemorySize = sizeInBytes }
    fun setMaxDiskSize(sizeInBytes: Long) = apply { this.maxDiskSize = sizeInBytes }
    fun setTtl(ttl: Long, unit: TimeUnit = TimeUnit.SECONDS) = apply {
      if (ttl > 0L) {
        this.ttl = ttl
        this.ttlUnit = unit
      } else {
        Log.w(
          TAG,
          "setTtl: ttl must be > 0, ignored. current value remains ${this.ttl}${this.ttlUnit}"
        )
      }
    }

    /**
     * 设置 SDK 全局默认加载策略。
     *
     * 注意：[LoadPolicy.DEFAULT] 不可传入此方法——它是注解层的哨兵值，
     * 表示"跟随 SDK 全局策略"，在全局配置中无意义，传入会被忽略。
     */
    fun setLoadPolicy(loadPolicy: LoadPolicy) = apply {
      if (loadPolicy != LoadPolicy.DEFAULT) {
        this.loadPolicy = loadPolicy
      } else {
        Log.w(TAG, "setLoadPolicy: LoadPolicy.DEFAULT is not allowed, ignored.")
      }
    }

    /**
     * 设置自定义的调试日志处理器。
     * 传入 DefaultSdkLogger(true) 可直接开启控制台标准调试日志输出。
     */
    fun setLogger(logger: ISdkLogger) = apply { this.logger = logger }

    /**
     * 设置自定义的关键节点监控器，用于对接主 App 的 APM 系统或埋点告警。
     */
    fun setMonitor(monitor: ICacheMonitor) = apply { this.monitor = monitor }

    /**
     * 开启后自动过滤连续相同的响应数据，避免 UI 重复刷新。
     * 依赖 DTO 的 equals() 实现。
     */
    fun setDeduplicateResponse(enabled: Boolean) = apply { this.deduplicateResponse = enabled }

    /**
     * 设置缓存写入判断器，由业务方决定当前响应是否允许写入缓存。
     * 未设置时默认所有成功响应均写缓存。
     */
    fun setCachePredicate(predicate: CachePredicate) = apply { this.cachePredicate = predicate }

    /**
     * 设置缓存 Key 转换器。
     * 返回 null 或空白字符串时，沿用 SDK 默认规则。
     */
    fun setCacheKeyTransformer(transformer: CacheKeyTransformer) = apply {
      this.cacheKeyTransformer = transformer
    }

    /**
     * 关闭磁盘缓存 AES-GCM-256 加密（默认开启）。
     *
     * 仅在明确不需要数据保护的场景（如纯内网设备、调试环境）下调用。
     * 关闭后磁盘文件以明文存储，可被 root 设备或 ADB backup 直接读取。
     */
    fun disableDiskEncryption() = apply { this.enableDiskEncryption = false }

    fun build(): Config {
      val safeTtl = if (ttl > 0L) ttl else DEFAULT_TTL
      val safeTtlUnit = if (ttl > 0L) ttlUnit else DEFAULT_TTL_UNIT
      return Config(
        cacheDir = cacheDir,
        maxMemorySize = maxMemorySize,
        maxDiskSize = maxDiskSize,
        ttl = safeTtl,
        ttlUnit = safeTtlUnit,
        loadPolicy = loadPolicy,
        logger = logger,
        monitor = monitor,
        deduplicateResponse = deduplicateResponse,
        cachePredicate = cachePredicate,
        cacheKeyTransformer = cacheKeyTransformer,
        enableDiskEncryption = enableDiskEncryption,
      )
    }

    private fun calculateDefaultMemorySize(): Int {
      val maxMemory = Runtime.getRuntime().maxMemory()
      val targetSize = (maxMemory / MEMORY_FRACTION_DIVIDER).toInt()
      return min(max(targetSize, MIN_MEMORY_CACHE_SIZE), MAX_MEMORY_CACHE_SIZE)
    }
  }

  @Volatile
  private var _config: Config? = null

  val config: Config
    get() = _config ?: error("NetworkCache must be initialized via NetworkCache.init() first.")

  private fun initConfig(config: Config) {
    this._config = config
  }

  /**
   * 静态内部类持有者
   */
  private object Holder {
    val INSTANCE = NetworkCache()
  }

  companion object {
    private const val TAG = "NetworkCache"
    private const val KB = 1024
    private const val MB = 1024 * KB
    private const val DEFAULT_DISK_CACHE_SIZE = 50L * MB
    private const val DEFAULT_TTL = 5 * 60L
    private val DEFAULT_TTL_UNIT = TimeUnit.SECONDS
    private val DEFAULT_LOAD_POLICY = LoadPolicy.CACHE_THEN_NETWORK
    private const val MEMORY_FRACTION_DIVIDER = 16
    private const val MIN_MEMORY_CACHE_SIZE = 4 * MB
    private const val MAX_MEMORY_CACHE_SIZE = 32 * MB

    @JvmStatic
    fun get(): NetworkCache = Holder.INSTANCE

    @JvmStatic
    fun isInitialized(): Boolean = Holder.INSTANCE._config != null

    /**
     * 初始化入口，重复调用无效。
     */
    @JvmStatic
    fun init(config: Config) {
      if (isInitialized()) return
      Holder.INSTANCE.initConfig(config)
      CacheStorageManager.preload()
    }

    /**
     * 清除所有缓存数据，包括内存和磁盘缓存。
     *
     * 注意：
     * 当发生用户登录、退出登录、账号切换等身份变化场景时，
     * 建议主动调用此方法，避免复用旧缓存数据。
     */
    @JvmStatic
    fun clearAll() {
      CacheStorageManager.clearAll()
    }
  }
}
