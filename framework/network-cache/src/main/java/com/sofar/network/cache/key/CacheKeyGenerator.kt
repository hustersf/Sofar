package com.sofar.network.cache.key

import okhttp3.Request
import okhttp3.RequestBody
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8

object CacheKeyGenerator {

  private const val SEPARATOR = "|"

  /**
   * 支持参与 CacheKey 计算的 application 子类型。
   */
  private val SUPPORTED_SUBTYPES = setOf(
    "json",
    "x-www-form-urlencoded",
    "xml"
  )

  /**
   * 为 HTTP 请求生成唯一缓存 Key。
   *
   * Key 组成：
   * 1. HTTP Method
   * 2. Request Url（包含 Query 参数）
   * 3. 文本类型 RequestBody（Json/Form/Xml）
   */
  fun generate(request: Request): String {
    val rawKey = buildString {
      append(request.method)
      append(SEPARATOR)
      append(request.url)
      extractBody(request)?.let { body ->
        append(SEPARATOR)
        append(body)
      }
    }

    return rawKey.encodeUtf8().md5().hex()
  }

  private fun extractBody(request: Request): String? {
    val body = request.body ?: return null
    if (body.isOneShot()) {
      return null
    }

    if (!isSupportedBody(body)) {
      return null
    }

    return runCatching {
      val buffer = Buffer()
      body.writeTo(buffer)
      buffer.readUtf8()
    }.getOrNull()
  }

  private fun isSupportedBody(body: RequestBody): Boolean {
    val contentType = body.contentType() ?: return false
    return contentType.type == "text" || contentType.subtype in SUPPORTED_SUBTYPES
  }
}
