package com.sofar.kmp.network

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.darwin.DarwinClientEngineConfig
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValuesOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.reinterpret
import platform.CoreFoundation.CFArrayCreate
import platform.CoreFoundation.CFDataRef
import platform.CoreFoundation.kCFAllocatorDefault
import platform.Foundation.NSData
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSURLAuthenticationMethodServerTrust
import platform.Foundation.NSURLCredential
import platform.Foundation.NSURLSessionAuthChallengeCancelAuthenticationChallenge
import platform.Foundation.NSURLSessionAuthChallengePerformDefaultHandling
import platform.Foundation.NSURLSessionAuthChallengeUseCredential
import platform.Foundation.create
import platform.Foundation.credentialForTrust
import platform.Foundation.serverTrust
import platform.Security.SecCertificateCreateWithData
import platform.Security.SecPolicyCreateSSL
import platform.Security.SecTrustEvaluateWithError
import platform.Security.SecTrustSetAnchorCertificates
import platform.Security.SecTrustSetAnchorCertificatesOnly
import platform.Security.SecTrustSetPolicies

actual fun platform() = "iOS"

@OptIn(ExperimentalForeignApi::class)
@Suppress("UNCHECKED_CAST")
actual fun HttpClientConfig<*>.configureTrustAll() {
  (this as? HttpClientConfig<DarwinClientEngineConfig>)?.engine {
    handleChallenge { session, task, challenge, completionHandler ->
      val serverTrust = challenge.protectionSpace.serverTrust
      if (challenge.protectionSpace.authenticationMethod == NSURLAuthenticationMethodServerTrust && serverTrust != null) {
        // 核心逻辑：直接使用服务器提供的信任对象创建凭据，从而信任该连接
        val credential = NSURLCredential.credentialForTrust(serverTrust)
        completionHandler(NSURLSessionAuthChallengeUseCredential.convert(), credential)
      } else {
        completionHandler(NSURLSessionAuthChallengePerformDefaultHandling.convert(), null)
      }
    }
  }
}

private const val EXPECTED_CERTIFICATE_COUNT = 1L

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Suppress("UNCHECKED_CAST")
actual fun HttpClientConfig<*>.configureCustomCertificate(
  pemContent: String
) {
  // 在外部预先清洗和解析证书，避免在 handleChallenge 回调中重复解析提高性能
  val base64String = pemContent
    .replace("-----BEGIN CERTIFICATE-----", "")
    .replace("-----END CERTIFICATE-----", "")
    .replace("\n", "")
    .replace("\r", "")
    .trim()

  val localCertData = NSData.create(base64EncodedString = base64String, options = 0u)
  val anchorCert = localCertData?.let {
    SecCertificateCreateWithData(null, it.toCFDataRef())
  }

  (this as? HttpClientConfig<DarwinClientEngineConfig>)?.engine {
    handleChallenge { _, _, challenge, completionHandler ->
      val serverTrust = challenge.protectionSpace.serverTrust
      val authMethod = challenge.protectionSpace.authenticationMethod

      val isServerTrust = authMethod == NSURLAuthenticationMethodServerTrust
      val canEvaluate = isServerTrust && serverTrust != null && anchorCert != null

      if (canEvaluate) {
        // 忽略域名/IP 匹配
        val policy = SecPolicyCreateSSL(true, null)
        SecTrustSetPolicies(serverTrust, policy)

        val anchors = memScoped {
          val certsArray = cValuesOf(anchorCert)
          CFArrayCreate(
            kCFAllocatorDefault,
            certsArray.ptr.reinterpret(),
            EXPECTED_CERTIFICATE_COUNT,
            null
          )
        }

        SecTrustSetAnchorCertificates(serverTrust, anchors)
        SecTrustSetAnchorCertificatesOnly(serverTrust, true)

        if (SecTrustEvaluateWithError(serverTrust, null)) {
          val credential = NSURLCredential.credentialForTrust(serverTrust!!)
          completionHandler(NSURLSessionAuthChallengeUseCredential.convert(), credential)
        } else {
          completionHandler(NSURLSessionAuthChallengeCancelAuthenticationChallenge.convert(), null)
        }
      } else {
        completionHandler(NSURLSessionAuthChallengePerformDefaultHandling.convert(), null)
      }
    }
  }
}

@OptIn(ExperimentalForeignApi::class)
fun NSData.toCFDataRef(): CFDataRef {
  // 将 NSData 转换为 CFDataRef 并增加引用计数
  return CFBridgingRetain(this) as CFDataRef
}
