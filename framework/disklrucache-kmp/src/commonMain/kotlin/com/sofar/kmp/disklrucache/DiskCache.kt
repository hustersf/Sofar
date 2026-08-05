package com.sofar.kmp.disklrucache

import okio.BufferedSink
import okio.BufferedSource
import okio.Source

interface DiskCache : AutoCloseable {

  val valueCount: Int

  val size: Long

  val maxSize: Long

  fun get(key: String): String?

  fun getBytes(key: String): ByteArray?

  fun snapshot(key: String): Snapshot?

  fun edit(key: String): Editor?

  fun put(key: String, value: String)

  fun put(key: String, value: ByteArray)

  fun put(key: String, value: Source)

  fun remove(key: String): Boolean

  fun clear()

  interface Snapshot : AutoCloseable {

    fun source(index: Int = 0): BufferedSource

    fun bytes(index: Int = 0): ByteArray

    fun string(index: Int = 0): String
  }

  interface Editor : AutoCloseable {

    fun sink(index: Int = 0): BufferedSink

    fun commit()

    fun abort()
  }
}
