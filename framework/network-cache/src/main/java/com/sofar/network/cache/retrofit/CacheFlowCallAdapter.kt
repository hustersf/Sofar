package com.sofar.network.cache.retrofit

import com.google.gson.Gson
import com.sofar.network.cache.NetworkCache
import com.sofar.network.cache.key.CacheKeyGenerator
import com.sofar.network.cache.monitor.ICacheMonitor
import com.sofar.network.cache.monitor.ISdkLogger
import com.sofar.network.cache.policy.LoadPolicy
import com.sofar.network.cache.predicate.CachePredicate
import com.sofar.network.cache.storage.CacheStorageManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

internal class CacheFlowCallAdapter<R>(
  private val responseType: Type,
  private val resolvedOptions: ResolvedCacheOptions,
  private val dispatcher: CoroutineDispatcher,
  private val gson: Gson
) : CallAdapter<R, Flow<R>> {

  companion object {
    private const val TAG = "CacheFlow"
  }

  override fun responseType(): Type = responseType

  @Suppress("TooGenericExceptionCaught")
  override fun adapt(call: Call<R>): Flow<R> {
    val config = NetworkCache.get().config
    val flow = callbackFlow {
      val logger = config.logger
      val monitor = config.monitor

      val request = call.request()
      val urlPath = request.url.encodedPath
      val cacheKey = CacheKeyGenerator.generate(request)
      // 请求级策略优先
      val finalLoadPolicy = request.tag(LoadPolicy::class.java)
        ?.takeIf { it != LoadPolicy.DEFAULT }
        ?: resolvedOptions.loadPolicy

      val doReadCache = {
        readCache(
          cacheKey = cacheKey,
          urlPath = urlPath,
          logger = logger,
          monitor = monitor
        )
      }
      val doRequestNetwork = {
        requestNetwork(
          call = call,
          cacheKey = cacheKey,
          urlPath = urlPath,
          logger = logger,
          monitor = monitor,
          cachePredicate = config.cachePredicate
        )
      }

      var activeCall: Call<R>? = null
      when (finalLoadPolicy) {
        LoadPolicy.CACHE_ONLY -> {
          doReadCache()
          close()
        }

        LoadPolicy.NETWORK_ONLY -> {
          activeCall = doRequestNetwork()
        }

        LoadPolicy.CACHE_THEN_NETWORK -> {
          doReadCache()
          activeCall = doRequestNetwork()
        }

        else -> {
          logger.e(TAG, "invalid load policy: $finalLoadPolicy, fallback to network_only")
          activeCall = doRequestNetwork()
        }
      }

      // Flow 关闭时取消请求
      awaitClose {
        val executingCall = activeCall
        if (executingCall != null && !executingCall.isCanceled) {
          logger.d(TAG, "flow closed: $urlPath")
          executingCall.cancel()
        }
      }
    }
    val targetFlow = if (config.deduplicateResponse) {
      flow.distinctUntilChanged()
    } else {
      flow
    }
    return targetFlow.flowOn(dispatcher)
  }

  @Suppress("TooGenericExceptionCaught")
  private fun ProducerScope<R>.requestNetwork(
    call: Call<R>,
    cacheKey: String,
    urlPath: String,
    logger: ISdkLogger,
    monitor: ICacheMonitor,
    cachePredicate: CachePredicate?
  ): Call<R> {
    return enqueueNetworkCall(
      sourceCall = call,
      responseType = responseType,
      onNetworkSuccess = { body, networkCost ->
        monitor.onNetworkSuccess(urlPath, networkCost)
        try {
          val shouldCache = cachePredicate?.shouldCache(body as Any) ?: true
          if (shouldCache) {
            CacheStorageManager.put(
              cacheKey = cacheKey,
              responseBodyBytes = gson.toJson(body).toByteArray(Charsets.UTF_8),
              ttlMillis = resolvedOptions.ttlMillis
            )
          }
        } catch (e: Exception) {
          monitor.onCacheWriteFailed(urlPath, e)
          logger.e(TAG, "cache write failed: $urlPath", e)
        }
      },
      onNetworkFailure = { throwable, networkCost ->
        monitor.onNetworkFailed(urlPath, throwable, networkCost)
        logger.e(TAG, "network failed: $urlPath", throwable)
      }
    )
  }

  @Suppress("TooGenericExceptionCaught")
  private fun ProducerScope<R>.readCache(
    cacheKey: String,
    urlPath: String,
    logger: ISdkLogger,
    monitor: ICacheMonitor
  ) {
    try {
      val cacheEntity = CacheStorageManager.get(cacheKey)
      if (cacheEntity == null) {
        monitor.onCacheMiss(urlPath)
        return
      }

      val createTime = cacheEntity.createTime
      val now = System.currentTimeMillis()
      val isExpired = now - createTime > resolvedOptions.ttlMillis

      if (isExpired) {
        monitor.onCacheExpired(urlPath)
        logger.d(TAG, "cache expired: $urlPath")
        return
      }

      val cacheData = gson.fromJson<R>(
        String(cacheEntity.responseBodyBytes, Charsets.UTF_8),
        responseType
      ) ?: throw EmptyBodyException("cache data is null")
      val result = trySend(cacheData)
      if (result.isSuccess) {
        monitor.onCacheHit(urlPath)
        logger.d(TAG, "cache hit: $urlPath")
      }
    } catch (e: Exception) {
      monitor.onCacheReadFailed(urlPath, e)
      logger.e(TAG, "cache read failed: $urlPath", e)
    }
  }
}
