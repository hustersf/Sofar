package com.sofar.network2.internal

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class MockTokenInterceptor : Interceptor {

  // 模拟服务器端的 Token 状态
  private var serverToken = ""

  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()
    val url = request.url.toString()
    val authHeader = request.header("Authorization") ?: ""

    return when {
      // 1. 模拟初始获取 Token 接口
      url.contains("auth/token") -> {
        serverToken = "TOKEN_A"
        makeJsonResponse(chain, getAuthSuccessJson(serverToken, "REFRESH_A"))
      }

      // 2. 模拟刷新 Token 接口
      url.contains("auth/refreshToken") -> {
        serverToken = "TOKEN_B"
        makeJsonResponse(chain, getAuthSuccessJson(serverToken, "REFRESH_B"))
      }

      // 3. 模拟业务接口 (例如获取 Banner)
      url.contains("/banner/json") -> {
        when {
          // 场景 A: 压根没带 Token
          authHeader.isEmpty() -> {
            makeJsonResponse(chain, getErrorJson(401, "No Token"))
          }
          // 场景 B: No Token 场景
          authHeader.contains("TOKEN_A") -> {
            makeJsonResponse(chain, getBannerSuccessJson())
          }
          // 场景 C: Token 过期场景
          authHeader.contains("TOKEN_B") -> {
            makeJsonResponse(chain, getBannerSuccessJson())
          }

          else -> makeJsonResponse(chain, getErrorJson(401, "Invalid Token"))
        }
      }

      else -> chain.proceed(request)
    }
  }

  private fun getAuthSuccessJson(at: String, rt: String) = """
        {
            "errorCode": 0,
            "errorMsg": "Open API Get Access Token successfully.",
            "data": {
                "accessToken": "$at",
                "refreshToken": "$rt",
                 "tokenType": "bearer",
                 "expiresIn": 7200
            }
        }
    """.trimIndent()

  /**
   * 构造满足你要求的成功 Banner 列表 JSON
   */
  private fun getBannerSuccessJson(): String {
    return """
        {
            "data": [
                {
                    "id": 1,
                    "title": "2026 全新 SDK 架构方案",
                    "desc": "基于协程与 OkHttp 的最佳实践",
                    "imagePath": "https://example.com/image1.png",
                    "url": "https://example.com/article1",
                    "order": 1
                },
                {
                    "id": 2,
                    "title": "Retrofit 2.11.0 深度解析",
                    "desc": "探索现代网络请求库的新特性",
                    "imagePath": "https://example.com/image2.png",
                    "url": "https://example.com/article2",
                    "order": 2
                }
            ],
            "errorCode": 0,
            "errorMsg": ""
        }
        """.trimIndent()
  }

  /**
   * 构造通用的错误响应 JSON
   */
  private fun getErrorJson(code: Int, msg: String): String {
    return """
        {
            "data": [],
            "errorCode": $code,
            "errorMsg": "$msg"
        }
        """.trimIndent()
  }

  private fun makeJsonResponse(chain: Interceptor.Chain, json: String): Response {
    return Response.Builder()
      .request(chain.request())
      .protocol(Protocol.HTTP_1_1)
      .code(200)
      .message("OK")
      .body(json.toResponseBody("application/json".toMediaType()))
      .build()
  }
}