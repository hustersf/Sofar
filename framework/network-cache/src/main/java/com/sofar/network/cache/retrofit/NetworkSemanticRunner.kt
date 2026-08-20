package com.sofar.network.cache.retrofit

import kotlinx.coroutines.channels.ProducerScope
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.lang.reflect.Type

internal fun isUnitType(type: Type?): Boolean {
  return type == Unit::class.java
}

@Suppress("UNCHECKED_CAST")
internal fun <R> ProducerScope<R>.enqueueNetworkCall(
  sourceCall: Call<R>,
  responseType: Type? = null,
  onNetworkSuccess: (body: R, costMs: Long) -> Unit = { _, _ -> },
  onNetworkFailure: (throwable: Throwable, costMs: Long) -> Unit = { _, _ -> }
): Call<R> {
  val networkStartTime = System.currentTimeMillis()
  val activeCall = sourceCall.clone()
  activeCall.enqueue(object : Callback<R> {
    override fun onResponse(call: Call<R>, response: Response<R>) {
      val networkCost = System.currentTimeMillis() - networkStartTime
      if (response.isSuccessful) {
        val body = response.body()
        if (body == null && !isUnitType(responseType)) {
          close(EmptyBodyException())
          return
        }

        val value = body ?: Unit as R
        onNetworkSuccess(value, networkCost)
        val result = trySend(value)
        if (result.isSuccess) {
          close()
        } else {
          close(result.exceptionOrNull())
        }
      } else {
        val httpException = HttpException(response)
        onNetworkFailure(httpException, networkCost)
        close(httpException)
      }
    }

    override fun onFailure(call: Call<R>, t: Throwable) {
      if (activeCall.isCanceled) {
        return
      }
      val networkCost = System.currentTimeMillis() - networkStartTime
      onNetworkFailure(t, networkCost)
      close(t)
    }
  })
  return activeCall
}
