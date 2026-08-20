package com.sofar.network.cache.retrofit

/**
 * 响应状态码为 200，但响应体为 null 的异常
 */
class EmptyBodyException(message: String = "Response body is null") : Exception(message)
