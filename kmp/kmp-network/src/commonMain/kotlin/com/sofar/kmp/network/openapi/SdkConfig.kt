package com.sofar.kmp.network.openapi

import com.sofar.kmp.network.engine.NetworkConfig
import kotlin.concurrent.Volatile

/**
 * 网络库全局配置类
 *
 * 采用 Builder 模式构建，用于管理 BaseUrl、超时间、安全证书以及各类鉴权 Token。
 * 建议在应用启动时初始化一个全局单例。
 *
 * @property baseUrl 接口基础地址
 * @property connectTimeout 网络连接超时时间（毫秒）
 * @property debugMode 是否开启调试模式（开启后通常会打印网络日志）
 * @property tokenFetcher 自动刷新 Token 的委托接口，用于处理鉴权失败场景
 * @property tokenRetry 发生授权错误时是否允许自动重试
 * @property trustedCert 自定义信任的证书 (PEM 格式)，用于 SSL 校验
 * @property trustAll 是否信任所有证书（仅建议在测试环境下开启，慎用！）
 */
class SdkConfig private constructor(builder: Builder) {

  val baseUrl: String = builder.baseUrl
  val connectTimeout: Long = builder.connectTimeout
  val debugMode: Boolean = builder.debugMode
  val tokenFetcher: TokenFetcher? = builder.tokenFetcher
  val tokenRetry: Boolean = builder.tokenRetry
  val trustedCert: String? = builder.trustedCert
  val trustAll: Boolean = builder.trustAll

  /**
   * 访问令牌 (Bearer Token 等)
   */
  @Volatile
  var accessToken: String? = builder.accessToken

  /**
   * (Cross-Site Request Forgery) 防护令牌。
   */
  @Volatile
  var csrfToken: String? = builder.csrfToken

  /**
   * 持久化 Cookie 字符串
   */
  @Volatile
  var cookie: String? = builder.cookie

  @Suppress("TooManyFunctions")
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
    fun setConnectTimeout(connectTimeout: Long) = apply { this.connectTimeout = connectTimeout }
    fun setDebugMode(debug: Boolean) = apply { this.debugMode = debug }
    fun setTokenFetcher(fetcher: TokenFetcher?) = apply { this.tokenFetcher = fetcher }
    fun setTokenRetry(tokenRetry: Boolean) = apply { this.tokenRetry = tokenRetry }
    fun setAccessToken(token: String) = apply { this.accessToken = token }
    fun setCsrfToken(csrfToken: String) = apply { this.csrfToken = csrfToken }
    fun setCookie(cookie: String) = apply { this.cookie = cookie }
    fun setTrustedCert(pem: String?) = apply { this.trustedCert = pem }
    fun setTrustAll(trustAll: Boolean) = apply { this.trustAll = trustAll }

    fun build() = SdkConfig(this)
  }

  internal fun toNetworkConfig(): NetworkConfig {
    return NetworkConfig.Builder()
      .setBaseUrl(this.baseUrl)
      .setConnectTimeout(this.connectTimeout)
      .setDebugMode(this.debugMode)
      .setTrustedCert(this.trustedCert)
      .setTrustAll(this.trustAll)
      .build()
  }

  companion object {
    private const val DEFAULT_TIMEOUT_MS = 10000L

    inline fun build(block: Builder.() -> Unit = {}): SdkConfig {
      return Builder().apply(block).build()
    }
  }
}