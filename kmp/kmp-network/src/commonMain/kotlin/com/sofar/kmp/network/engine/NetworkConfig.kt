package com.sofar.kmp.network.engine

/**
 * 网络库全局配置类
 *
 * 采用 Builder 模式构建，用于管理 BaseUrl、超时间、安全证书等
 * 建议在应用启动时初始化一个全局单例。
 *
 * @property baseUrl 接口基础地址
 * @property connectTimeout 网络连接超时时间（毫秒）
 * @property debugMode 是否开启调试模式（开启后通常会打印网络日志）
 * @property trustedCert 自定义信任的证书 (PEM 格式)，用于 SSL 校验
 * @property trustAll 是否信任所有证书（仅建议在c c`环境下开启，慎用！）
 * @property interceptors 自定义网络拦截器列表
 */
class NetworkConfig private constructor(builder: Builder) {

  val baseUrl: String = builder.baseUrl
  val connectTimeout: Long = builder.connectTimeout
  val debugMode: Boolean = builder.debugMode
  val trustedCert: String? = builder.trustedCert
  val trustAll: Boolean = builder.trustAll
  val interceptors: List<NetworkInterceptor> = builder.interceptors.toList()

  class Builder {
    internal var baseUrl: String = "https://api.yourcloud.com"
    internal var connectTimeout: Long = DEFAULT_TIMEOUT_MS
    internal var debugMode: Boolean = false
    internal var trustedCert: String? = null
    internal var trustAll: Boolean = false
    internal val interceptors = mutableListOf<NetworkInterceptor>()

    fun setBaseUrl(url: String) = apply { this.baseUrl = url }
    fun setConnectTimeout(connectTimeout: Long) = apply { this.connectTimeout = connectTimeout }
    fun setDebugMode(debug: Boolean) = apply { this.debugMode = debug }
    fun setTrustedCert(pem: String?) = apply { this.trustedCert = pem }
    fun setTrustAll(trustAll: Boolean) = apply { this.trustAll = trustAll }
    fun addInterceptor(interceptor: NetworkInterceptor) = apply {
      this.interceptors.add(interceptor)
    }

    fun build() = NetworkConfig(this)
  }

  companion object {
    private const val DEFAULT_TIMEOUT_MS = 10000L

    inline fun build(block: Builder.() -> Unit = {}): NetworkConfig {
      return Builder().apply(block).build()
    }
  }
}