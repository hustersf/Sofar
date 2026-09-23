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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import okhttp3.Request
import okhttp3.RequestBody
import okio.Buffer
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.milliseconds

internal class CacheFlowCallAdapter<R>(
  private val responseType: Type,
  private val resolvedOptions: ResolvedCacheOptions,
  private val dispatcher: CoroutineDispatcher,
  private val gson: Gson
) : CallAdapter<R, Flow<R>> {

  companion object {
    private const val TAG = "CacheFlow"
    private const val MAX_WAIT_TIME = 500L
    private const val DELAY_STEP = 5L
  }

  override fun responseType(): Type = responseType

  @Suppress("TooGenericExceptionCaught")
  override fun adapt(call: Call<R>): Flow<R> {
    val config = NetworkCache.get().config
    // 声明标志位，初始为安全
    val isPipeSafe = AtomicBoolean(true)

    val flow = flow {
      val logger = config.logger
      val monitor = config.monitor

      val request = call.request()
      val urlPath = request.url.encodedPath
      val cacheKey = CacheKeyGenerator.generate(
        method = request.method,
        url = request.url.toString(),
        body = request.extractCacheKeyBody(),
        transformer = config.cacheKeyTransformer
      )
      // 请求级策略优先
      val finalLoadPolicy = request.tag(LoadPolicy::class.java)
        ?.takeIf { it != LoadPolicy.DEFAULT }
        ?: resolvedOptions.loadPolicy

      val doReadCache = suspend {
        readCache(
          cacheKey = cacheKey,
          urlPath = urlPath,
          logger = logger,
          monitor = monitor,
          isPipeSafe = isPipeSafe
        )
      }
      val doRequestNetwork = suspend {
        requestNetwork(
          call = call,
          cacheKey = cacheKey,
          urlPath = urlPath,
          logger = logger,
          monitor = monitor,
          cachePredicate = config.cachePredicate
        )
      }

      when (finalLoadPolicy) {
        LoadPolicy.CACHE_ONLY -> {
          doReadCache()
        }

        LoadPolicy.NETWORK_ONLY -> {
          doRequestNetwork()
        }

        LoadPolicy.CACHE_THEN_NETWORK -> {
          doReadCache()
          // 核心刹车逻辑：捕获异常并检查标志位
          try {
            doRequestNetwork()
          } catch (t: Throwable) {
            if (!isPipeSafe.get()) {
              logger.d(TAG, "pipe not safe, waiting for cache consumption: $urlPath")
              val startTime = System.currentTimeMillis()
              while (!isPipeSafe.get() && (System.currentTimeMillis() - startTime < MAX_WAIT_TIME)) {
                delay(DELAY_STEP.milliseconds)
              }
              if (!isPipeSafe.get()) {
                logger.e(TAG, "cache consumption timeout: $urlPath")
              } else {
                logger.d(TAG, "cache consumed, releasing pipe: $urlPath")
              }
            }
            throw t
          }
        }

        else -> {
          logger.e(TAG, "invalid load policy: $finalLoadPolicy, fallback to network_only")
          doRequestNetwork()
        }
      }
    }

    val targetFlow = if (config.deduplicateResponse) {
      flow.distinctUntilChanged()
    } else {
      flow
    }

    return targetFlow
      .flowOn(dispatcher)
      .onEach {
        // 缓存已穿过 flowOn 异步通道，可释放等待中的网络异常。
        isPipeSafe.set(true)
      }
  }

  @Suppress("TooGenericExceptionCaught")
  private suspend fun FlowCollector<R>.requestNetwork(
    call: Call<R>,
    cacheKey: String,
    urlPath: String,
    logger: ISdkLogger,
    monitor: ICacheMonitor,
    cachePredicate: CachePredicate?
  ) {
    val body = call.executeNetworkCall(
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
      },
      onCancelled = {
        logger.d(TAG, "flow closed: $urlPath")
      }
    )
    emit(body)
  }

  @Suppress("TooGenericExceptionCaught")
  private suspend fun FlowCollector<R>.readCache(
    cacheKey: String,
    urlPath: String,
    logger: ISdkLogger,
    monitor: ICacheMonitor,
    isPipeSafe: AtomicBoolean
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

      // 发射前加锁
      isPipeSafe.set(false)
      emit(cacheData)
      monitor.onCacheHit(urlPath)
      logger.d(TAG, "cache hit: $urlPath")
    } catch (e: Exception) {
      monitor.onCacheReadFailed(urlPath, e)
      logger.e(TAG, "cache read failed: $urlPath", e)
    }
  }
}

private val CACHE_KEY_SUPPORTED_BODY_SUBTYPES = setOf(
  "json",
  "x-www-form-urlencoded",
  "xml"
)

private fun Request.extractCacheKeyBody(): String? {
  val requestBody = body ?: return null
  if (requestBody.isOneShot()) {
    return null
  }

  if (!requestBody.isSupportedCacheKeyBody()) {
    return null
  }

  return runCatching {
    val buffer = Buffer()
    requestBody.writeTo(buffer)
    buffer.readUtf8()
  }.getOrNull()
}

private fun RequestBody.isSupportedCacheKeyBody(): Boolean {
  val contentType = contentType() ?: return false
  return contentType.type == "text" || contentType.subtype in CACHE_KEY_SUPPORTED_BODY_SUBTYPES
}
