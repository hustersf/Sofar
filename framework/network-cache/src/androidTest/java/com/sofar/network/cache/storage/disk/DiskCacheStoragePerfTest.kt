package com.sofar.network.cache.storage.disk

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sofar.network.cache.storage.CacheEntity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DiskCacheStoragePerfTest {

  private lateinit var storageEncrypted: DiskCacheStorage
  private lateinit var storagePlain: DiskCacheStorage

  @Before
  fun setUp() {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    storageEncrypted = DiskCacheStorage(
      cacheDir = context.cacheDir.resolve("perf_encrypted"),
      maxDiskSize = 50L * 1024 * 1024,
      enableEncryption = true,
    )
    storageEncrypted.warmUp()
    storagePlain = DiskCacheStorage(
      cacheDir = context.cacheDir.resolve("perf_plain"),
      maxDiskSize = 50L * 1024 * 1024,
      enableEncryption = false,
    )
    storagePlain.warmUp()
  }

  @After
  fun tearDown() {
    storageEncrypted.clear()
    storageEncrypted.close()
    storagePlain.clear()
    storagePlain.close()
  }

  // ─── 少量大文件：重点测 I/O 吞吐与加解密耗时 ───

  @Test
  fun fewLargeFiles_encrypted() {
    measure(storage = storageEncrypted, fileSizeKb = 512, fileCount = 50, tag = "大文件-加密")
  }

  @Test
  fun fewLargeFiles_plain() {
    measure(storage = storagePlain, fileSizeKb = 512, fileCount = 50, tag = "大文件-明文")
  }

  // ─── 大量小文件：重点测 LRU 索引 + 文件句柄开销 ───

  @Test
  fun manySmallFiles_encrypted() {
    measure(storage = storageEncrypted, fileSizeKb = 4, fileCount = 1000, tag = "小文件-加密")
  }

  @Test
  fun manySmallFiles_plain() {
    measure(storage = storagePlain, fileSizeKb = 4, fileCount = 1000, tag = "小文件-明文")
  }

  // ─── LRU eviction：总写入量超过 maxDiskSize，观察 eviction 是否拖慢写入 ───

  @Test
  fun evictionUnderPressure_encrypted() {
    // 200 × 256KB = 51.2MB > 50MB maxDiskSize，必然触发 eviction
    measure(storage = storageEncrypted, fileSizeKb = 256, fileCount = 200, tag = "eviction-加密")
  }

  @Test
  fun evictionUnderPressure_plain() {
    measure(storage = storagePlain, fileSizeKb = 256, fileCount = 200, tag = "eviction-明文")
  }

  // ─────────────────────────────────────────────

  private fun measure(
    storage: DiskCacheStorage,
    fileSizeKb: Int,
    fileCount: Int,
    tag: String
  ) {
    val payload = ByteArray(fileSizeKb * 1024) { it.toByte() }
    val now = System.currentTimeMillis()

    // 写入
    val writeStart = System.nanoTime()
    repeat(fileCount) { i ->
      storage.put(CacheEntity("key_$i", payload, now))
    }
    val writeMs = (System.nanoTime() - writeStart) / 1_000_000

    // 顺序读
    val seqStart = System.nanoTime()
    repeat(fileCount) { i -> storage.get("key_$i") }
    val seqMs = (System.nanoTime() - seqStart) / 1_000_000

    // 随机读（模拟真实 LRU 命中分布）
    val shuffled = (0 until fileCount).shuffled()
    val rndStart = System.nanoTime()
    shuffled.forEach { i -> storage.get("key_$i") }
    val rndMs = (System.nanoTime() - rndStart) / 1_000_000

    val totalKb = fileSizeKb * fileCount
    Log.d(
      TAG,
      "[$tag]  ${fileSizeKb}KB × $fileCount files  total=${totalKb}KB\n" +
          "  write    ${writeMs}ms  avg ${writeMs / fileCount}ms/file\n" +
          "  seq read ${seqMs}ms  avg ${seqMs / fileCount}ms/file\n" +
          "  rnd read ${rndMs}ms  avg ${rndMs / fileCount}ms/file"
    )
  }

  private companion object {
    const val TAG = "DiskCachePerfTest"
  }
}
