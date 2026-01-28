package com.sofar.kmp.network.core

class SdkConfig private constructor(builder: Builder) {

  val id: String = builder.id
  val apiKey: String = builder.apiKey
  val apiSecret: String = builder.apiSecret
  val grantType: String = builder.grantType
  val code: String = builder.code
  val baseUrl: String = builder.baseUrl
  val connectTimeout: Long = builder.connectTimeout
  val debugMode: Boolean = builder.debugMode
  val tokenProvider: TokenProvider? = builder.tokenProvider

  class Builder {
    internal var id: String = ""
    internal var apiKey: String = ""
    internal var apiSecret: String = ""
    internal var grantType: String = "client_credentials"
    internal var code: String = ""
    internal var baseUrl: String = "https://api.yourcloud.com"
    internal var connectTimeout: Long = DEFAULT_TIMEOUT_MS
    internal var debugMode: Boolean = false
    internal var tokenProvider: TokenProvider? = null

    fun setId(id: String) = apply { this.id = id }
    fun setApiKey(key: String) = apply { this.apiKey = key }
    fun setApiSecret(key: String) = apply { this.apiSecret = key }
    fun setGrantType(grantType: String) = apply { this.grantType = grantType }
    fun setCode(code: String) = apply { this.code = code }
    fun setBaseUrl(url: String) = apply { this.baseUrl = url }
    fun setDebugMode(debug: Boolean) = apply { this.debugMode = debug }
    fun setTokenProvider(provider: TokenProvider?) = apply { this.tokenProvider = provider }

    fun build() = SdkConfig(this)
  }

  companion object {
    private const val DEFAULT_TIMEOUT_MS = 15000L

    inline fun build(block: Builder.() -> Unit = {}): SdkConfig {
      return Builder().apply(block).build()
    }
  }
}