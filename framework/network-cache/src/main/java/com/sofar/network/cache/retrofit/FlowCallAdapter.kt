package com.sofar.network.cache.retrofit

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

internal class FlowCallAdapter<R>(
  private val responseType: Type,
  private val dispatcher: CoroutineDispatcher
) : CallAdapter<R, Flow<R>> {

  override fun responseType(): Type = responseType

  override fun adapt(call: Call<R>): Flow<R> {
    return callbackFlow {
      val activeCall = enqueueNetworkCall(
        sourceCall = call,
        responseType = responseType
      )

      awaitClose {
        if (!activeCall.isCanceled) {
          activeCall.cancel()
        }
      }
    }.flowOn(dispatcher)
  }
}
