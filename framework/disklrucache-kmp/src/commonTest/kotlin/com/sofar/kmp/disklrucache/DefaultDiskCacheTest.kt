package com.sofar.kmp.disklrucache

import okio.Buffer
import okio.Path
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import okio.use
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DefaultDiskCacheTest {

  private lateinit var fileSystem: FakeFileSystem

  private fun createCache(
    valueCount: Int = SINGLE_VALUE_COUNT,
  ): DefaultDiskCache {
    return DefaultDiskCache(
      directory = CACHE_DIRECTORY,
      fileSystem = fileSystem,
      maxSize = CACHE_MAX_SIZE,
      valueCount = valueCount,
    )
  }

  @BeforeTest
  fun setUp() {
    fileSystem = FakeFileSystem().apply {
      emulateUnix()
    }
  }

  @AfterTest
  fun tearDown() {
    fileSystem.checkNoOpenFiles()
  }

  @Test
  fun putAndGetString() {
    createCache().use { cache ->
      cache.put(TEST_KEY, TEST_VALUE)

      assertEquals(
        TEST_VALUE,
        cache.get(TEST_KEY),
      )
    }
  }

  @Test
  fun putAndGetBytes() {
    createCache().use { cache ->
      cache.put(TEST_KEY, TEST_BYTES)

      assertContentEquals(
        TEST_BYTES,
        cache.getBytes(TEST_KEY),
      )
    }
  }

  @Test
  fun putAndGetSource() {
    createCache().use { cache ->
      cache.put(TEST_KEY, Buffer().writeUtf8(TEST_VALUE))

      assertEquals(TEST_VALUE, cache.get(TEST_KEY))
    }
  }

  @Test
  fun removeEntry() {
    createCache().use { cache ->
      cache.put(TEST_KEY, TEST_VALUE)

      assertTrue(
        cache.remove(TEST_KEY),
      )

      assertNull(
        cache.get(TEST_KEY),
      )
    }
  }

  @Test
  fun clearCache() {
    createCache().use { cache ->
      cache.put(FIRST_KEY, FIRST_VALUE)
      cache.put(SECOND_KEY, SECOND_VALUE)

      cache.clear()

      assertNull(cache.get(FIRST_KEY))
      assertNull(cache.get(SECOND_KEY))
    }
  }

  @Test
  fun snapshotRead() {
    createCache().use { cache ->
      cache.put(TEST_KEY, TEST_VALUE)

      cache.snapshot(TEST_KEY)!!.use { snapshot ->
        assertEquals(
          TEST_VALUE,
          snapshot.string(),
        )
      }
    }
  }

  @Test
  fun snapshotSourceRead() {
    createCache().use { cache ->
      cache.put(TEST_KEY, TEST_VALUE)
      cache.snapshot(TEST_KEY)!!.use { snapshot ->
        snapshot.source().use { source ->
          assertEquals(
            TEST_VALUE,
            source.readUtf8(),
          )
        }
      }
    }
  }

  @Test
  fun editorCommit() {
    createCache().use { cache ->
      cache.edit(TEST_KEY)!!.use { editor ->
        editor.sink().writeUtf8(TEST_VALUE)
        editor.commit()
      }

      assertEquals(
        TEST_VALUE,
        cache.get(TEST_KEY),
      )
    }
  }

  @Test
  fun editorAbort() {
    createCache().use { cache ->
      cache.edit(TEST_KEY)!!.use { editor ->
        editor.sink().writeUtf8(TEST_VALUE)
        editor.abort()
      }

      assertNull(
        cache.get(TEST_KEY),
      )
    }
  }

  @Test
  fun multiValueEntry() {
    createCache(
      valueCount = MULTI_VALUE_COUNT,
    ).use { cache ->

      cache.edit(TEST_KEY)!!.use { editor ->
        editor.sink(FIRST_INDEX)
          .writeUtf8(FIRST_MULTI_VALUE)

        editor.sink(SECOND_INDEX)
          .writeUtf8(SECOND_MULTI_VALUE)

        editor.commit()
      }

      cache.snapshot(TEST_KEY)!!.use { snapshot ->
        assertEquals(
          FIRST_MULTI_VALUE,
          snapshot.string(FIRST_INDEX),
        )

        assertEquals(
          SECOND_MULTI_VALUE,
          snapshot.string(SECOND_INDEX),
        )
      }
    }
  }

  @Test
  fun putFailsWhenValueCountGreaterThanOne() {
    createCache(
      valueCount = MULTI_VALUE_COUNT,
    ).use { cache ->
      assertFailsWith<IllegalStateException> {
        cache.put(
          TEST_KEY,
          TEST_VALUE,
        )
      }
    }
  }

  companion object {
    private const val CACHE_MAX_SIZE = 1024L * 1024L

    private const val SINGLE_VALUE_COUNT = 1
    private const val MULTI_VALUE_COUNT = 2

    private const val FIRST_INDEX = 0
    private const val SECOND_INDEX = 1

    private const val TEST_KEY = "user"

    private const val TEST_VALUE = "hello"

    private val TEST_BYTES = TEST_VALUE.encodeToByteArray()

    private const val FIRST_KEY = "a"
    private const val SECOND_KEY = "b"

    private const val FIRST_VALUE = "1"
    private const val SECOND_VALUE = "2"

    private const val FIRST_MULTI_VALUE = "meta"
    private const val SECOND_MULTI_VALUE = "data"

    private val CACHE_DIRECTORY: Path = "/cache".toPath()
  }
}
