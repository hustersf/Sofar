package com.sofar.network.openapi

import com.google.gson.Gson
import com.sofar.network.openapi.auth.TokenProvider

data class SdkConfig private constructor(
  val id: String,
  val apiKey: String,
  val apiSecret: String,
  val baseUrl: String,
  val connectTimeout: Long,
  val debugMode: Boolean,
  val tokenProvider: TokenProvider?,
  val gson: Gson = Gson(),
) {

  fun mutateBaseUrl(newBaseUrl: String): SdkConfig {
    return this.copy(baseUrl = newBaseUrl)
  }

  class Builder {
    private var id: String = ""
    private var apiKey: String = ""
    private var apiSecret: String = ""
    private var baseUrl: String = "https://api.yourcloud.com"
    private var connectTimeout: Long = 15000L
    private var debugMode: Boolean = false
    private var tokenProvider: TokenProvider? = null
    private var gson: Gson = Gson()

    fun setId(id: String) = apply { this.id = id }
    fun setApiKey(key: String) = apply { this.apiKey = key }
    fun setApiSecret(key: String) = apply { this.apiSecret = key }
    fun setBaseUrl(url: String) = apply { this.baseUrl = url }
    fun setDebugMode(debug: Boolean) = apply { this.debugMode = debug }
    fun setTokenProvider(provider: TokenProvider) = apply {
      this.tokenProvider = provider
    }

    fun setGson(gson: Gson) = apply { this.gson = gson }

    fun build() =
      SdkConfig(id, apiKey, apiSecret, baseUrl, connectTimeout, debugMode, tokenProvider, gson)
  }

  companion object {
    inline fun build(block: Builder.() -> Unit = {}): SdkConfig {
      return Builder().apply(block).build()
    }

    @JvmStatic
    fun builder(): SdkConfig.Builder = Builder()
  }
}
