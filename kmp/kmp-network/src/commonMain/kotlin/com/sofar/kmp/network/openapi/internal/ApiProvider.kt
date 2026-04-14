package com.sofar.kmp.network.openapi.internal

import com.sofar.kmp.network.engine.NetworkEngine
import com.sofar.kmp.network.openapi.SdkConfig
import com.sofar.kmp.network.openapi.TokenManager
import com.sofar.kmp.network.openapi.api.BannerApi

abstract class ApiProvider(val config: SdkConfig) {
  internal abstract val tokenManager: TokenManager?
  internal abstract val engine: NetworkEngine

  internal val executor: ApiExecutor by lazy {
    ApiExecutor(engine, config, tokenManager)
  }

  val banner: BannerApi by lazy { BannerApi(executor) }
}