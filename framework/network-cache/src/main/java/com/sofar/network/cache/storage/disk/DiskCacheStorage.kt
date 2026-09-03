package com.sofar.network.cache.storage.disk

import com.sofar.kmp.disklrucache.DefaultDiskCache
import com.sofar.kmp.disklrucache.DiskCache
import com.sofar.network.cache.storage.CacheEntity
import com.sofar.network.cache.storage.CacheStorage
import okio.Buffer
import okio.Path.Companion.toPath
import java.io.File

class DiskCacheStorage(
  private val cacheDir: File,
  private val maxDiskSize: Long,
  private val enableEncryption: Boolean,
) : CacheStorage {

  private val cache: DiskCache by lazy {
    cacheDir.mkdirs()
    DefaultDiskCache(
      directory = cacheDir.absolutePath.toPath(),
      maxSize = maxDiskSize,
    )
  }

  private val diskCipher: DiskCacheCipher? by lazy {
    if (enableEncryption) DiskCacheCipher(cacheDir) else null
  }

  fun warmUp() {
    get("pre_warm_dummy_key")
    diskCipher?.warmUpKey()
  }

  private fun encrypt(data: ByteArray, cacheKey: String): ByteArray? =
    diskCipher?.encrypt(data, cacheKey.encodeToByteArray())

  private fun decrypt(data: ByteArray, cacheKey: String): ByteArray? =
    diskCipher?.decrypt(data, cacheKey.encodeToByteArray())

  /**
   * 磁盘存储格式：
   *   明文模式：[createTime(8字节 Long)][responseBodyBytes]
   *   加密模式：encrypt([createTime(8字节 Long)][responseBodyBytes])
   *             = [IV(12字节)][密文 + GCM Tag(16字节)]
   */
  override fun get(cacheKey: String): CacheEntity? {
    return runCatching {
      val stored = cache.getBytes(cacheKey) ?: return@runCatching null
      val payload = if (enableEncryption) {
        decrypt(stored, cacheKey) ?: return@runCatching null
      } else {
        stored
      }
      if (payload.size < Long.SIZE_BYTES) return@runCatching null
      val buffer = Buffer().write(payload)
      val createTime = buffer.readLong()
      val bodyBytes = buffer.readByteArray()
      CacheEntity(cacheKey, bodyBytes, createTime)
    }.getOrNull()
  }

  override fun put(entity: CacheEntity) {
    runCatching {
      val payload = Buffer()
        .writeLong(entity.createTime)
        .write(entity.responseBodyBytes)
        .readByteArray()
      val stored = if (enableEncryption) {
        encrypt(payload, entity.cacheKey) ?: return@runCatching
      } else {
        payload
      }
      cache.put(entity.cacheKey, stored)
    }
  }

  override fun remove(cacheKey: String) {
    runCatching {
      cache.remove(cacheKey)
    }
  }

  override fun clear() {
    runCatching {
      cache.clear()
    }
  }

  fun close() {
    runCatching {
      cache.close()
    }
  }
}
