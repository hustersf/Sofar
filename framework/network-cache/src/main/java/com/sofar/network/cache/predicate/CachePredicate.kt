package com.sofar.network.cache.predicate

/**
 * 缓存写入判断器，由业务方决定网络响应是否允许写入缓存。
 * 未配置时默认全部缓存。
 *
 * 示例：
 * ```kotlin
 * .setCachePredicate { response ->
 *     when (response) {
 *         is BaseResponse<*> -> response.errorCode == 0
 *         else -> true
 *     }
 * }
 * ```
 */
fun interface CachePredicate {
  /**
   * @return true 写缓存，false 跳过
   */
  fun shouldCache(response: Any): Boolean
}
