package com.sofar.kmp.disklrucache

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import okio.BufferedSink
import okio.BufferedSource
import okio.FileSystem
import okio.IOException
import okio.Path
import okio.SYSTEM
import okio.Source
import okio.buffer
import okio.use
import kotlin.coroutines.CoroutineContext

class DefaultDiskCache(
  directory: Path,
  override val valueCount: Int = 1,
  override val maxSize: Long,
  private val fileSystem: FileSystem = FileSystem.SYSTEM,
  private val appVersion: Int = 1,
  cleanupCoroutineContext: CoroutineContext = Dispatchers.IO,
) : DiskCache {

  private val cache = DiskLruCache(
    fileSystem = fileSystem,
    directory = directory,
    cleanupCoroutineContext = cleanupCoroutineContext,
    maxSize = maxSize,
    appVersion = appVersion,
    valueCount = valueCount,
  )

  init {
    require(maxSize > 0L) { "maxSize <= 0" }
    require(valueCount > 0) { "valueCount <= 0" }
  }

  override fun get(key: String): String? {
    return withSnapshot(key) { it.string() }
  }

  override fun getBytes(key: String): ByteArray? {
    return withSnapshot(key) { it.bytes() }
  }

  override fun snapshot(key: String): DiskCache.Snapshot? {
    val snapshot = cache[key] ?: return null
    return SnapshotImpl(fileSystem, snapshot, valueCount)
  }

  override fun edit(key: String): DiskCache.Editor? {
    val editor = cache.edit(key) ?: return null
    return EditorImpl(fileSystem, editor, valueCount)
  }

  override val size: Long
    get() = cache.size()

  override fun put(key: String, value: String) {
    editAndCommit(key) { sink ->
      sink.writeUtf8(value)
    }
  }

  override fun put(key: String, value: ByteArray) {
    editAndCommit(key) { sink ->
      sink.write(value)
    }
  }

  override fun put(key: String, value: Source) {
    editAndCommit(key) { sink ->
      sink.writeAll(value)
    }
  }

  override fun remove(key: String): Boolean = cache.remove(key)

  override fun clear() {
    cache.evictAll()
  }

  override fun close() {
    cache.close()
  }

  private inline fun editAndCommit(
    key: String,
    block: (BufferedSink) -> Unit,
  ) {
    requireSingleValuePut()
    val editor = edit(key) ?: throw IOException("Unable to edit cache entry: $key")
    try {
      block(editor.sink(0))
      editor.commit()
    } finally {
      editor.close()
    }
  }

  private inline fun <T> withSnapshot(
    key: String,
    block: (DiskCache.Snapshot) -> T,
  ): T? {
    val snapshot = snapshot(key) ?: return null
    return snapshot.use(block)
  }

  private fun requireSingleValuePut() {
    check(valueCount == 1) {
      "put() only supports valueCount = 1. Use edit() for multi-value entries."
    }
  }

  private class SnapshotImpl(
    private val fileSystem: FileSystem,
    private val snapshot: DiskLruCache.Snapshot,
    private val valueCount: Int,
  ) : DiskCache.Snapshot {

    private var closed = false

    override fun source(index: Int): BufferedSource {
      checkIndex(index, valueCount)
      check(!closed) { "snapshot is closed" }
      return fileSystem.source(snapshot.file(index)).buffer()
    }

    override fun bytes(index: Int): ByteArray {
      return source(index).use { it.readByteArray() }
    }

    override fun string(index: Int): String {
      return source(index).use { it.readUtf8() }
    }

    override fun close() {
      if (!closed) {
        closed = true
        snapshot.close()
      }
    }
  }

  private class EditorImpl(
    private val fileSystem: FileSystem,
    private val editor: DiskLruCache.Editor,
    private val valueCount: Int,
  ) : DiskCache.Editor {

    private var closed = false
    private val sinks = arrayOfNulls<BufferedSink>(valueCount)

    override fun sink(index: Int): BufferedSink {
      checkIndex(index, valueCount)
      check(!closed) { "editor is closed" }

      val current = sinks[index]
      if (current != null) {
        return current
      }

      return fileSystem.sink(editor.file(index)).buffer().also { sinks[index] = it }
    }

    override fun commit() {
      check(!closed) { "editor is closed" }
      closeSinks()
      editor.commit()
      closed = true
    }

    override fun abort() {
      if (closed) {
        return
      }
      closeSinks()
      editor.abort()
      closed = true
    }

    override fun close() {
      abort()
    }

    private fun closeSinks() {
      for (index in sinks.indices) {
        val sink = sinks[index]
        if (sink != null) {
          sink.close()
          sinks[index] = null
        }
      }
    }
  }
}

private fun checkIndex(index: Int, valueCount: Int) {
  require(index in 0 until valueCount) {
    "index must be in 0 until $valueCount: $index"
  }
}
