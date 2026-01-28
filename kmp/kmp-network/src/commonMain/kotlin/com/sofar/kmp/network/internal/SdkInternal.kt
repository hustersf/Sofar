package com.sofar.kmp.network.internal

import com.sofar.kmp.network.core.SdkConfig

internal object SdkInternal {
  lateinit var config: SdkConfig
    private set


  private var initialized = false

  fun inject(sdkConfig: SdkConfig) {
    if (initialized) return
    this.config = sdkConfig
    this.initialized = true
    TokenManager.setProvider(sdkConfig.tokenProvider)
  }
}