package com.sofar.network.cache.retrofit

import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.lang.reflect.Type
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal fun isUnitType(type: Type?): Boolean {
  return type == Unit::class.java
}

/**
 * 将 Retrofit Call 转为标准挂起函数，支持结构化并发取消
 */
@Suppress("UNCHECKED_CAST")
internal suspend fun <R> Call<R>.executeNetworkCall(
  responseType: Type? = null,
  onNetworkSuccess: (body: R, costMs: Long) -> Unit = { _, _ -> },
  onNetworkFailure: (throwable: Throwable, costMs: Long) -> Unit = { _, _ -> },
  onCancelled: () -> Unit = {}
): R {
  return suspendCancellableCoroutine { continuation ->
    val networkStartTime = System.currentTimeMillis()
    val activeCall = this.clone()

    continuation.invokeOnCancellation {
      onCancelled()
      activeCall.cancel()
    }

    activeCall.enqueue(object : Callback<R> {
      override fun onResponse(call: Call<R>, response: Response<R>) {
        val networkCost = System.currentTimeMillis() - networkStartTime
        if (response.isSuccessful) {
          val body = response.body()
          if (body == null && !isUnitType(responseType)) {
            continuation.resumeWithException(EmptyBodyException())
            return
          }

          val value = body ?: Unit as R
          onNetworkSuccess(value, networkCost)
          continuation.resume(value)
        } else {
          val httpException = HttpException(response)
          onNetworkFailure(httpException, networkCost)
          continuation.resumeWithException(httpException)
        }
      }

      override fun onFailure(call: Call<R>, t: Throwable) {
        if (activeCall.isCanceled) {
          return
        }
        val networkCost = System.currentTimeMillis() - networkStartTime
        onNetworkFailure(t, networkCost)
        continuation.resumeWithException(t)
      }
    })
  }
}
