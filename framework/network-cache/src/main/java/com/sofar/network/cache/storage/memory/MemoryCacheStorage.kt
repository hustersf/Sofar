package com.sofar.network.cache.storage.memory

import androidx.collection.LruCache
import com.sofar.network.cache.storage.CacheEntity
import com.sofar.network.cache.storage.CacheStorage

class MemoryCacheStorage(
  private val maxMemorySize: Int
) : CacheStorage {
  // AndroidX 的 LruCache，它内部自带了线程安全锁（synchronized）
  private val lruCache = object : LruCache<String, CacheEntity>(maxMemorySize) {

    // 核心重写：告诉 LruCache 如何计算每个 Entry 占用的内存大小
    override fun sizeOf(key: String, value: CacheEntity): Int {
      return value.memorySize
    }
  }

  override fun get(cacheKey: String): CacheEntity? {
    return lruCache[cacheKey]
  }

  override fun put(entity: CacheEntity) {
    lruCache.put(entity.cacheKey, entity)
  }

  override fun remove(cacheKey: String) {
    lruCache.remove(cacheKey)
  }

  override fun clear() {
    lruCache.evictAll()
  }

  fun getCurrentSize(): Int = lruCache.size()
}
