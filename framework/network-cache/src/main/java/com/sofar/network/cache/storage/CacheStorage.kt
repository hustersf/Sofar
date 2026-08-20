package com.sofar.network.cache.storage

interface CacheStorage {

  fun get(cacheKey: String): CacheEntity?

  fun put(entity: CacheEntity)

  fun remove(cacheKey: String)

  fun clear()
}
