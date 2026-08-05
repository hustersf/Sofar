package com.sofar.kmp.disklrucache

import kotlinx.coroutines.CoroutineDispatcher
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

internal val CoroutineContext.dispatcher: CoroutineDispatcher?
  get() = get(ContinuationInterceptor) as? CoroutineDispatcher

/** @see forEach */
internal inline fun <T> List<T>.forEachIndices(action: (T) -> Unit) {
  for (i in indices) {
    action(get(i))
  }
}
