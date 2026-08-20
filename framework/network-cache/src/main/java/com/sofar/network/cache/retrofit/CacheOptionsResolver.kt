package com.sofar.network.cache.retrofit

import com.sofar.network.cache.NetworkCache
import com.sofar.network.cache.policy.LoadPolicy

internal data class ResolvedCacheOptions(
  val ttlMillis: Long,
  val loadPolicy: LoadPolicy
)

internal object CacheOptionsResolver {

  fun resolve(
    cacheable: Cacheable,
    config: NetworkCache.Config
  ): ResolvedCacheOptions {
    val ttlMillis = resolveTtlMillis(cacheable, config)
    val loadPolicy = resolveLoadPolicy(cacheable, config)
    return ResolvedCacheOptions(
      ttlMillis = ttlMillis,
      loadPolicy = loadPolicy
    )
  }

  private fun resolveTtlMillis(
    cacheable: Cacheable,
    config: NetworkCache.Config
  ): Long {
    // ttl <= 0 时回退为全局默认，避免异常
    if (cacheable.ttl <= 0L) {
      return config.ttlMillis
    }
    return cacheable.unit.toMillis(cacheable.ttl)
  }

  private fun resolveLoadPolicy(
    cacheable: Cacheable,
    config: NetworkCache.Config
  ): LoadPolicy {
    return if (cacheable.loadPolicy == LoadPolicy.DEFAULT) {
      config.loadPolicy
    } else {
      cacheable.loadPolicy
    }
  }
}
