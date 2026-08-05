package com.sofar.kmp.network.openapi.internal

import com.sofar.kmp.network.engine.NetworkEngine
import com.sofar.kmp.network.openapi.SdkConfig
import com.sofar.kmp.network.openapi.TokenManager
import com.sofar.kmp.network.openapi.api.model.ApiResponse
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.defaultForFilePath
import io.ktor.http.isSuccess
import io.ktor.serialization.JsonConvertException
import io.ktor.util.date.getTimeMillis
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeFully
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import kotlinx.serialization.SerializationException
import kotlin.coroutines.cancellation.CancellationException

internal class ApiExecutor(
  private val engine: NetworkEngine,
  private val config: SdkConfig,
  private val tokenManager: TokenManager? = null,
) {

  companion object {
    const val IO_BUFFER_SIZE_BYTES = 8192L
  }

  suspend inline fun <reified T> HttpResponse.safeParse(): ApiResponse<T> {
    if (!status.isSuccess()) {
      return ApiResponse(
        data = null,
        errorCode = status.value,
        errorMsg = "HTTP Error: ${status.value} ${status.description}"
      )
    }
    val rawText = bodyAsText()
    return NetworkEngine.sdkJson.decodeFromString(rawText)
  }

  /**
   * 通用的 Token 重试逻辑包装器
   * @param initialResponse 第一次请求的结果
   * @param requestBlock 重新发起请求的逻辑
   * @param parseBlock 如何解析 Response
   */
  suspend inline fun <T> withTokenRetry(
    initialResponse: HttpResponse,
    crossinline requestBlock: suspend () -> HttpResponse,
    crossinline parseBlock: suspend (HttpResponse) -> ApiResponse<T>,
  ): ApiResponse<T> {
    if (tokenManager == null) return parseBlock(initialResponse)
    val oldToken = tokenManager.getCurrentToken()
    val firstResult = parseBlock(initialResponse)
    if (config.tokenRetry && tokenManager.isExpired(initialResponse, firstResult.errorCode)) {
      if (tokenManager.refreshAndGet(oldToken) != null) {
        return parseBlock(requestBlock())
      }
    }
    return firstResult
  }

  @Suppress("TooGenericExceptionCaught")
  private suspend inline fun <T> runNetworkCatching(
    crossinline action: suspend () -> ApiResponse<T>
  ): ApiResponse<T> {
    return try {
      action()
    } catch (e: Exception) {
      if (config.debugMode) {
        println("❌ [KMP Network Error] Type: ${e::class.simpleName}, Message: ${e.messageWithCause()}")
      }

      // 统一异常匹配与错误码细分
      val (errorCode, fallbackMsg) = when (e) {
        // 协程取消必须上抛，不作为错误码处理
        is CancellationException -> throw e

        // 超时系列
        is HttpRequestTimeoutException,
        is ConnectTimeoutException,
        is SocketTimeoutException -> {
          ApiResponse.ERROR_TIMEOUT to "Connection timed out."
        }

        // 数据反序列化失败系列
        is JsonConvertException,
        is SerializationException -> {
          ApiResponse.ERROR_PARSING_FAILED to "Data parsing failed."
        }

        // 连接失败系列（通过关键字符匹配 IOException）
        is IOException -> {
          val msg = e.messageWithCause()
          if (isConnectionIssue(msg)) {
            ApiResponse.ERROR_CONNECT_FAILED to "Server unreachable."
          } else {
            ApiResponse.NETWORK_ERROR to "Network I/O error."
          }
        }

        // 其它未知错误兜底（退化回 -1）
        else -> {
          ApiResponse.NETWORK_ERROR to "Network Error"
        }
      }

      ApiResponse(
        data = null,
        errorCode = errorCode,
        errorMsg = e.message ?: fallbackMsg
      )
    }
  }

  // Android/iOS 真实断网,无网络,ssl异常
  @Suppress("ComplexCondition")
  private fun isConnectionIssue(msg: String): Boolean {
    return msg.contains("connect", ignoreCase = true) ||
        msg.contains("host", ignoreCase = true) ||
        msg.contains("offline", ignoreCase = true) ||
        // ssl 相关
        msg.contains("ssl", ignoreCase = true) ||
        msg.contains("cert", ignoreCase = true) ||
        msg.contains("trust", ignoreCase = true)
  }

  private fun Throwable.messageWithCause(): String {
    return listOfNotNull(
      this::class.simpleName,
      message,
      cause?.let { it::class.simpleName },
      cause?.message,
    ).joinToString(" ")
  }

  suspend inline fun <reified T> safeRequest(
    crossinline block: HttpRequestBuilder.() -> Unit,
  ): ApiResponse<T> = withContext(Dispatchers.IO) {
    runNetworkCatching {
      val client = engine.httpClient
      val response = client.request { block() }
      withTokenRetry(
        initialResponse = response,
        requestBlock = { client.request { block() } },
        parseBlock = { it.safeParse<T>() },
      )
    }
  }

  suspend fun safeDownload(
    directoryPath: String,
    fileName: String? = null,
    block: HttpRequestBuilder.() -> Unit,
  ): ApiResponse<String> = withContext(Dispatchers.IO) {
    runNetworkCatching {
      val client = engine.httpClient
      withTokenRetry(
        initialResponse = client.request { block() },
        requestBlock = { client.request { block() } },
        parseBlock = { response ->
          // 如果响应是 JSON，说明业务报错了，解析错误信息
          if (response.contentType()?.match(ContentType.Application.Json) == true) {
            val errorRes = response.safeParse<Unit>()
            ApiResponse(null, errorRes.errorCode, errorRes.errorMsg)
          } else {
            // 正常下载逻辑
            val path = download(response, directoryPath, fileName)
            ApiResponse(path, 0)
          }
        },
      )
    }
  }

  private suspend fun download(
    response: HttpResponse,
    directoryPath: String,
    fileName: String?,
  ): String {
    // 正常下载逻辑：使用 kotlinx-io 流式落盘
    val name = fileName ?: response.headers["Content-Disposition"]
      ?.substringAfter("filename=")?.trim()
      ?.removeSurrounding("\"") ?: "export_${getTimeMillis()}.zip"

    val parentDir = Path(directoryPath)
    val path = Path(parentDir, name)
    val channel: ByteReadChannel = response.body()
    // 获取系统的 sink（写入流）并开启缓冲区
    SystemFileSystem.sink(path).buffered().use { sink ->
      while (!channel.isClosedForRead) {
        val packet = channel.readRemaining(IO_BUFFER_SIZE_BYTES)
        sink.write(packet.readByteArray())
        sink.flush()
      }
    }
    return path.toString()
  }

  suspend inline fun <reified T> safeUpload(
    filePath: String,
    params: Map<String, String> = emptyMap(),
    crossinline block: HttpRequestBuilder.() -> Unit,
  ): ApiResponse<T> = withContext(Dispatchers.IO) {
    val path = Path(filePath)

    // 跨平台检查文件是否存在及获取大小
    val metadata = SystemFileSystem.metadataOrNull(path)
    if (metadata == null || !metadata.isRegularFile) {
      return@withContext ApiResponse(
        null,
        ApiResponse.NETWORK_ERROR,
        "File not found or invalid: $filePath"
      )
    }
    val fileSize = metadata.size

    val createRequestBuilder: HttpRequestBuilder.() -> Unit = {
      block()
      setBody(
        MultiPartFormDataContent(
          formData {
            params.forEach { (key, value) ->
              append(key, value)
            }

            appendInput(
              key = "file",
              headers = Headers.build {
                append(HttpHeaders.ContentType, ContentType.defaultForFilePath(filePath).toString())
                append(HttpHeaders.ContentDisposition, "filename=\"${path.name}\"")
              },
              size = fileSize,
            ) {
              buildPacket {
                val source = SystemFileSystem.source(path).buffered()
                val buffer = ByteArray(IO_BUFFER_SIZE_BYTES.toInt())
                source.use { s ->
                  while (true) {
                    val bytesRead = s.readAtMostTo(buffer)
                    if (bytesRead <= 0) break
                    writeFully(buffer, 0, bytesRead)
                  }
                }
              }
            }
          },
        ),
      )
    }

    runNetworkCatching {
      val client = engine.httpClient
      val response = client.request { createRequestBuilder() }
      withTokenRetry(
        initialResponse = response,
        requestBlock = { client.request { createRequestBuilder() } },
        parseBlock = { it.safeParse<T>() },
      )
    }
  }
}
