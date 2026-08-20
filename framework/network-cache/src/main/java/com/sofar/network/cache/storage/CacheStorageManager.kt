package com.sofar.network.cache.storage

import android.util.Log
import com.sofar.network.cache.NetworkCache
import com.sofar.network.cache.storage.disk.DiskCacheStorage
import com.sofar.network.cache.storage.memory.MemoryCacheStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object CacheStorageManager {

  private const val TAG = "CacheStorageManager"

  // TTL 阈值：小于 5 分钟的缓存不落盘
  private const val DISK_TTL_GATE_MILLIS = 5L * 60 * 1000

  // 大小阈值：响应体超过 512KB 不落盘
  private const val DISK_SIZE_GATE_BYTES = 512 * 1024

  private val config = NetworkCache.get().config

  private val memoryStorage by lazy {
    MemoryCacheStorage(
      config.maxMemorySize
    )
  }

  private val diskStorage by lazy {
    DiskCacheStorage(
      cacheDir = config.cacheDir,
      maxDiskSize = config.maxDiskSize,
      enableEncryption = config.enableDiskEncryption,
    )
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO.limitedParallelism(1))

  fun preload() {
    launchIo {
      // 后台预热 DiskCache 与加密软密钥，降低首次真实读写的冷启动开销
      diskStorage.warmUp()
    }
  }

  /**
   * 读取缓存：
   * Memory -> Disk
   */
  fun get(cacheKey: String): CacheEntity? {
    // L1 Memory Cache
    memoryStorage.get(cacheKey)?.let {
      return it
    }
    // L2 Disk Cache
    diskStorage.get(cacheKey)?.let { entity ->
      // 回填 Memory Cache
      memoryStorage.put(entity)
      return entity
    }
    return null
  }

  /**
   * 保存缓存：
   * Memory 同步写入。
   * Disk 由 SDK 内部策略决定：仅当 ttlMillis >= 5min 且 responseBodyBytes <= 512KB 时异步落盘。
   *
   * @param ttlMillis 该条缓存的 TTL（毫秒），用于策略判断
   */
  fun put(cacheKey: String, responseBodyBytes: ByteArray, ttlMillis: Long) {
    val entity = CacheEntity(
      cacheKey = cacheKey,
      responseBodyBytes = responseBodyBytes,
      createTime = System.currentTimeMillis()
    )

    // L1 Memory Cache：始终写入
    memoryStorage.put(entity)

    // L2 Disk Cache：满足 ttl 和 size 双门限才落盘
    val shouldPersistToDisk = ttlMillis >= DISK_TTL_GATE_MILLIS &&
        responseBodyBytes.size <= DISK_SIZE_GATE_BYTES
    if (shouldPersistToDisk) {
      launchIo {
        diskStorage.put(entity)
      }
    } else {
      config.logger.w(TAG, "skip disk cache: ttl=$ttlMillis, size=${responseBodyBytes.size}")
    }
  }

  /**
   * 删除单条缓存
   */
  fun remove(cacheKey: String) {
    memoryStorage.remove(cacheKey)
    launchIo {
      diskStorage.remove(cacheKey)
    }
  }

  /**
   * 清理所有缓存
   */
  fun clearAll() {
    memoryStorage.clear()
    launchIo {
      diskStorage.clear()
    }
  }

  /**
   * IO线程执行并统一异常保护
   */
  private inline fun launchIo(
    crossinline block: suspend () -> Unit
  ) {
    ioScope.launch {
      runCatching {
        block()
      }.onFailure {
        Log.w(TAG, "io operation failed", it)
      }
    }
  }
}
