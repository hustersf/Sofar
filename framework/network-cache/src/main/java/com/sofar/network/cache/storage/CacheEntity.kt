package com.sofar.network.cache.storage

class CacheEntity(
  val cacheKey: String,
  val responseBodyBytes: ByteArray,
  val createTime: Long
) {

  // L1 内存 LruCache 容量计算使用
  val memorySize: Int = cacheKey.length * Char.SIZE_BYTES + responseBodyBytes.size + Long.SIZE_BYTES
}
