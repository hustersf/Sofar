package com.sofar.kmp.network

import android.annotation.SuppressLint
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttpConfig
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

actual fun platform() = "Android"

@Suppress("UNCHECKED_CAST")
@SuppressLint("CustomX509TrustManager", "TrustAllX509TrustManager")
actual fun HttpClientConfig<*>.configureTrustAll() {
  (this as? HttpClientConfig<OkHttpConfig>)?.engine {
    config {
      val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>?, authType: String?) {
          // 忽略客户端证书验证
        }

        override fun checkServerTrusted(chain: Array<X509Certificate>?, authType: String?) {
          // 忽略服务器证书验证
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
      })

      val sslContext = SSLContext.getInstance("SSL").apply {
        init(null, trustAllCerts, SecureRandom())
      }

      sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
      hostnameVerifier { _, _ -> true }
    }
  }
}

@Suppress("UNCHECKED_CAST")
actual fun HttpClientConfig<*>.configureCustomCertificate(pemContents: List<String>) {
  (this as? HttpClientConfig<OkHttpConfig>)?.engine {
    config {
      val cf = CertificateFactory.getInstance("X.509")
      val keyStoreType = KeyStore.getDefaultType()
      val keyStore = KeyStore.getInstance(keyStoreType).apply {
        load(null, null)
        pemContents.forEachIndexed { index, pemContent ->
          val certificates = cf.generateCertificates(pemContent.byteInputStream(Charsets.UTF_8))
          certificates.forEachIndexed { certIndex, certificate ->
            setCertificateEntry("ca_${index}_$certIndex", certificate)
          }
        }
      }

      val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
      val tmf = TrustManagerFactory.getInstance(tmfAlgorithm).apply {
        init(keyStore)
      }
      val trustManagers = tmf.trustManagers
      val trustManager = trustManagers[0] as X509TrustManager
      val sslContext = SSLContext.getInstance("TLS").apply {
        init(null, trustManagers, SecureRandom())
      }
      sslSocketFactory(sslContext.socketFactory, trustManager)
      // 忽略域名/IP 匹配
      hostnameVerifier { _, _ -> true }
    }
  }
}
