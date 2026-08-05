package com.sofar.kmp.disklrucache

import okio.FileNotFoundException
import okio.FileSystem
import okio.IOException
import okio.Path
import okio.use

/** Create a new empty file. */
internal fun FileSystem.createFile(file: Path, mustCreate: Boolean = false) {
  if (mustCreate) {
    sink(file, mustCreate = true).use { }
  } else if (!exists(file)) {
    sink(file).use { }
  }
}

/** Tolerant delete, try to clear as many files as possible even after a failure. */
internal fun FileSystem.deleteContents(directory: Path) {
  var exception: IOException? = null
  val files = try {
    list(directory)
  } catch (_: FileNotFoundException) {
    return
  }
  for (file in files) {
    try {
      if (metadata(file).isDirectory) {
        deleteContents(file)
      }
      delete(file)
    } catch (e: IOException) {
      if (exception == null) {
        exception = e
      }
    }
  }
  if (exception != null) {
    throw exception
  }
}
