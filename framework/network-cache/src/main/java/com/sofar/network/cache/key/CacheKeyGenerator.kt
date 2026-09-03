package com.sofar.network.cache.key

import okio.ByteString.Companion.encodeUtf8

fun interface CacheKeyTransformer {
  fun transform(
    method: String,
    url: String,
    body: String?
  ): String?
}

object CacheKeyGenerator {

  private const val SEPARATOR = "|"

  /**
   * 为 HTTP 请求生成唯一缓存 Key，调用方负责传入可参与计算的 RequestBody 文本。
   *
   * Key 组成：
   * 1. HTTP Method
   * 2. Request Url（包含 Query 参数）
   * 3. RequestBody 文本
   */
  fun generate(
    method: String,
    url: String,
    body: String? = null,
    transformer: CacheKeyTransformer? = null
  ): String {
    val transformed = transformer?.transform(method, url, body)
    val rawKey = transformed?.takeIf { it.isNotBlank() } ?: buildRawKey(method, url, body)
    return rawKey.encodeUtf8().md5().hex()
  }

  private fun buildRawKey(method: String, url: String, body: String?): String {
    return buildString {
      append(method)
      append(SEPARATOR)
      append(url)
      body?.let {
        append(SEPARATOR)
        append(it)
      }
    }
  }
}
