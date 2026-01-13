package com.sofar.network2.internal

import android.content.Context
import com.sofar.network2.core.SdkConfig

internal object SdkInternal {
  lateinit var config: SdkConfig
    private set

  lateinit var appContext: Context
    private set

  private var initialized = false

  fun inject(context: Context, sdkConfig: SdkConfig) {
    if (initialized) return
    this.appContext = context.applicationContext
    this.config = sdkConfig
    this.initialized = true
    TokenManager.setProvider(sdkConfig.tokenProvider)
  }

  fun checkInit() {
    if (!initialized) throw IllegalStateException("SDK 尚未初始化，请先调用 OpenApiClient.init()")
  }
}