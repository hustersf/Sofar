package com.sofar.kmp.network.core

import kotlin.concurrent.Volatile

class SdkConfig private constructor(builder: Builder) {

  val baseUrl: String = builder.baseUrl
  val connectTimeout: Long = builder.connectTimeout
  val debugMode: Boolean = builder.debugMode
  val tokenFetcher: TokenFetcher? = builder.tokenFetcher
  val tokenRetry: Boolean = builder.tokenRetry
  val trustedCert: String? = builder.trustedCert
  val trustAll: Boolean = builder.trustAll

  @Volatile
  var accessToken: String? = builder.accessToken

  @Volatile
  var csrfToken: String? = builder.csrfToken

  @Volatile
  var cookie: String? = builder.cookie

  class Builder {
    internal var baseUrl: String = "https://api.yourcloud.com"
    internal var connectTimeout: Long = DEFAULT_TIMEOUT_MS
    internal var debugMode: Boolean = false
    internal var accessToken: String? = null
    internal var tokenFetcher: TokenFetcher? = null
    internal var tokenRetry: Boolean = true
    internal var csrfToken: String? = null
    internal var cookie: String? = null
    internal var trustedCert: String? = null
    internal var trustAll: Boolean = false

    fun setBaseUrl(url: String) = apply { this.baseUrl = url }
    fun setDebugMode(debug: Boolean) = apply { this.debugMode = debug }
    fun setTokenFetcher(fetcher: TokenFetcher?) = apply { this.tokenFetcher = fetcher }
    fun setTokenRetry(tokenRetry: Boolean) = apply { this.tokenRetry = tokenRetry }
    fun setAccessToken(token: String) = apply { this.accessToken = token }
    fun setCsrfToken(csrfToken: String) = apply { this.csrfToken = csrfToken }
    fun setCookie(cookie: String) = apply { this.cookie = cookie }
    fun setTrustedCert(pem: String) = apply { this.trustedCert = pem }
    fun setTrustAll(trustAll: Boolean) = apply { this.trustAll = trustAll }

    fun build() = SdkConfig(this)
  }

  companion object {
    private const val DEFAULT_TIMEOUT_MS = 10000L

    inline fun build(block: Builder.() -> Unit = {}): SdkConfig {
      return Builder().apply(block).build()
    }
  }
}
