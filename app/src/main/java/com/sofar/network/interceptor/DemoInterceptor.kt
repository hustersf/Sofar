package com.sofar.network.interceptor

import com.sofar.kmp.network.engine.NetworkChain
import com.sofar.kmp.network.engine.NetworkInterceptor
import com.sofar.kmp.network.engine.NetworkResponse

class AddTokenInterceptor : NetworkInterceptor {
  override fun intercept(chain: NetworkChain): NetworkResponse {
    val newReq = chain.request().newBuilder()
      .header("Authorization", "Bearer xyz123")
      .build()
    return chain.proceed(newReq)
  }
}

class RewriteInterceptor : NetworkInterceptor {
  override fun intercept(chain: NetworkChain): NetworkResponse {
    // 1. 请求前：强刷成测试环境 URL
    val newReq = chain.request().newBuilder().url("https://api.com").build()
    // 2. 拿到响应后：直接替换返回的文本
    val res = chain.proceed(newReq)
    return res.newBuilder().bodyString("""{"mock":true}""").build()
  }
}