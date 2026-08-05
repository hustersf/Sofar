package com.sofar.kmp.network.engine.plugin

import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

internal val ConcurrencyLimitPlugin =
  createClientPlugin(
    name = "ConcurrencyLimitPlugin",
    createConfiguration = ::ConcurrencyLimitConfig
  ) {
    val maxConcurrentRequests = pluginConfig.maxConcurrentRequests
    require(maxConcurrentRequests > 0) {
      "maxConcurrentRequests must be greater than 0"
    }
    val semaphore = Semaphore(maxConcurrentRequests)
    on(Send) { request ->
      semaphore.withPermit {
        proceed(request)
      }
    }
  }