package com.sofar.kmp.network.openapi

import com.sofar.kmp.network.engine.NetworkEngine
import com.sofar.kmp.network.openapi.internal.ApiProvider
import com.sofar.kmp.network.openapi.internal.OpenTokenManager
import com.sofar.kmp.network.openapi.internal.installOpenApiAuth

class OpenApiClient(config: SdkConfig = SdkConfig.build()) : ApiProvider(config) {

  override val tokenManager = OpenTokenManager(config)

  override val engine = NetworkEngine(
    config = config.toNetworkConfig(),
    httpClientConfig = {
      installOpenApiAuth(config)
    },
  )
}