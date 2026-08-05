package com.sofar.kmp.network.openapi

import com.sofar.kmp.network.engine.NetworkConfig
import com.sofar.kmp.network.engine.NetworkInterceptor
import kotlin.concurrent.Volatile

/**
 * 网络库全局配置类
 *
 * 采用 Builder 模式构建，用于管理 BaseUrl、超时间、安全证书以及各类鉴权 Token。
 * 建议在应用启动时初始化一个全局单例。
 *
 * @property baseUrl 接口基础地址
 * @property connectTimeout 网络连接超时时间（毫秒）
 * @property socketTimeout Socket 读写超时时间（毫秒）
 * @property requestTimeout 整个请求生命周期超时时间（毫秒），null 表示不限制
 * @property debugMode 是否开启调试模式（开启后通常会打印网络日志）
 * @property tokenFetcher 自动刷新 Token 的委托接口，用于处理鉴权失败场景
 * @property tokenRetry 发生授权错误时是否允许自动重试
 * @property trustedCerts 自定义信任的证书 (PEM 格式)，用于 SSL 校验
 * @property trustAll 是否信任所有证书（仅建议在测试环境下开启，慎用！）
 * @property interceptors 自定义网络拦截器列表
 */
class SdkConfig private constructor(builder: Builder) {

  val baseUrl: String = builder.baseUrl
  val connectTimeout: Long = builder.connectTimeout
  val socketTimeout: Long = builder.socketTimeout
  val requestTimeout: Long? = builder.requestTimeout
  val debugMode: Boolean = builder.debugMode
  val tokenFetcher: TokenFetcher? = builder.tokenFetcher
  val tokenRetry: Boolean = builder.tokenRetry
  val trustedCerts: List<String> = builder.trustedCerts.toList()
  val trustAll: Boolean = builder.trustAll
  val interceptors: List<NetworkInterceptor> = builder.interceptors.toList()

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
    internal var socketTimeout: Long = DEFAULT_TIMEOUT_MS
    internal var requestTimeout: Long? = null
    internal var debugMode: Boolean = false
    internal var accessToken: String? = null
    internal var tokenFetcher: TokenFetcher? = null
    internal var tokenRetry: Boolean = true
    internal var csrfToken: String? = null
    internal var cookie: String? = null
    internal var trustedCerts: List<String> = emptyList()
    internal var trustAll: Boolean = false
    internal val interceptors = mutableListOf<NetworkInterceptor>()

    fun setBaseUrl(url: String) = apply { this.baseUrl = url }
    fun setConnectTimeout(connectTimeout: Long) = apply { this.connectTimeout = connectTimeout }
    fun setSocketTimeout(socketTimeout: Long) = apply { this.socketTimeout = socketTimeout }
    fun setRequestTimeout(requestTimeout: Long?) = apply { this.requestTimeout = requestTimeout }
    fun setDebugMode(debug: Boolean) = apply { this.debugMode = debug }
    fun setTokenFetcher(fetcher: TokenFetcher?) = apply { this.tokenFetcher = fetcher }
    fun setTokenRetry(tokenRetry: Boolean) = apply { this.tokenRetry = tokenRetry }
    fun setAccessToken(token: String) = apply { this.accessToken = token }
    fun setCsrfToken(csrfToken: String) = apply { this.csrfToken = csrfToken }
    fun setCookie(cookie: String) = apply { this.cookie = cookie }
    fun setTrustedCerts(trustedCerts: List<String>) = apply { this.trustedCerts = trustedCerts }
    fun setTrustAll(trustAll: Boolean) = apply { this.trustAll = trustAll }
    fun addInterceptor(interceptor: NetworkInterceptor) = apply {
      this.interceptors.add(interceptor)
    }

    fun build() = SdkConfig(this)
  }

  internal fun toNetworkConfig(): NetworkConfig {
    val builder = NetworkConfig.Builder()
      .setBaseUrl(this.baseUrl)
      .setConnectTimeout(this.connectTimeout)
      .setDebugMode(this.debugMode)
      .setTrustedCerts(this.trustedCerts)
      .setTrustAll(this.trustAll)

    this.interceptors.forEach { interceptor ->
      builder.addInterceptor(interceptor)
    }

    return builder.build()
  }

  companion object {
    private const val DEFAULT_TIMEOUT_MS = 10000L

    inline fun build(block: Builder.() -> Unit = {}): SdkConfig {
      return Builder().apply(block).build()
    }
  }
}