package com.sofar.network2.core

class SdkConfig private constructor(
  val id: String,
  val apiKey: String,
  val apiSecret: String,
  val baseUrl: String,
  val connectTimeout: Long,
  val debugMode: Boolean,
  val tokenProvider: TokenProvider?,
) {
  class Builder {
    private var id: String = ""
    private var apiKey: String = ""
    private var apiSecret: String = ""
    private var baseUrl: String = "https://api.yourcloud.com"
    private var connectTimeout: Long = 15000L
    private var debugMode: Boolean = false
    private var tokenProvider: TokenProvider? = null

    fun setId(id: String) = apply { this.id = id }
    fun setApiKey(key: String) = apply { this.apiKey = key }
    fun setApiSecret(key: String) = apply { this.apiSecret = key }
    fun setBaseUrl(url: String) = apply { this.baseUrl = url }
    fun setDebugMode(debug: Boolean) = apply { this.debugMode = debug }
    fun setTokenProvider(provider: TokenProvider) = apply {
      this.tokenProvider = provider
    }

    fun build() = SdkConfig(id, apiKey, apiSecret, baseUrl, connectTimeout, debugMode, tokenProvider)
  }

  companion object {
    inline fun build(block: Builder.() -> Unit = {}): SdkConfig {
      return Builder().apply(block).build()
    }

    @JvmStatic
    fun builder(): SdkConfig.Builder = Builder()
  }
}