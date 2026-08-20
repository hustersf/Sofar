package com.sofar.network.cache.retrofit

import com.google.gson.Gson
import com.sofar.network.cache.NetworkCache
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class CacheFlowCallAdapterFactory private constructor(
  private val defaultDispatcher: CoroutineDispatcher,
  private val gson: Gson
) : CallAdapter.Factory() {

  override fun get(
    returnType: Type,
    annotations: Array<out Annotation>,
    retrofit: Retrofit
  ): CallAdapter<*, *>? {
    // 仅处理 Flow<T>
    val rawType = getRawType(returnType)
    if (rawType != Flow::class.java) {
      return null
    }

    // Flow 必须携带泛型参数
    if (returnType !is ParameterizedType) {
      return null
    }

    // 提取 Flow<T> 中的真实数据类型 T
    val dataType = getParameterUpperBound(0, returnType)
    // 默认兜底网络适配器
    val flowAdapter = FlowCallAdapter<Any>(dataType, defaultDispatcher)
    // 未添加 @Cacheable 注解时退化为纯网络
    val cacheable = annotations.filterIsInstance<Cacheable>().firstOrNull()
      ?: return flowAdapter

    // 缓存能力不可用时退化为纯网络
    if (!NetworkCache.isInitialized()) return flowAdapter

    // 创建缓存增强适配器
    val config = NetworkCache.get().config
    val resolvedOptions = CacheOptionsResolver.resolve(cacheable, config)
    val logger = config.logger
    logger.d(
      TAG,
      "cache adapter: $dataType, ttl=${resolvedOptions.ttlMillis}ms, policy=${resolvedOptions.loadPolicy}"
    )
    return CacheFlowCallAdapter<Any>(
      responseType = dataType,
      resolvedOptions = resolvedOptions,
      dispatcher = defaultDispatcher,
      gson = gson
    )
  }

  companion object {
    private const val TAG = "CacheFactory"
    fun create(
      dispatcher: CoroutineDispatcher = Dispatchers.IO,
      gson: Gson = Gson()
    ): CacheFlowCallAdapterFactory = CacheFlowCallAdapterFactory(dispatcher, gson)
  }
}
