package com.sofar.kmp.network.openapi.internal

import com.sofar.kmp.network.engine.NetworkEngine
import com.sofar.kmp.network.openapi.SdkConfig
import com.sofar.kmp.network.openapi.TokenManager
import com.sofar.kmp.network.openapi.api.model.ApiResponse
import io.ktor.client.call.body
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
import io.ktor.util.date.getTimeMillis
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeFully
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray

internal class ApiExecutor(
  private val engine: NetworkEngine,
  private val config: SdkConfig,
  private val tokenManager: TokenManager? = null,
) {

  companion object {
    const val NETWORK_ERROR = -1
    const val NETWORK_ERROR_MSG = "Network Error"
    const val IO_BUFFER_SIZE_BYTES = 8192L
  }

  suspend inline fun <reified T> HttpResponse.safeParse(): ApiResponse<T> {
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
  suspend inline fun <reified T> safeRequest(
    crossinline block: HttpRequestBuilder.() -> Unit,
  ): ApiResponse<T> = withContext(Dispatchers.IO) {
    try {
      val client = engine.httpClient
      val response = client.request { block() }
      withTokenRetry(
        initialResponse = response,
        requestBlock = { client.request { block() } },
        parseBlock = { it.safeParse<T>() },
      )
    } catch (e: Exception) {
      ApiResponse(null, NETWORK_ERROR, e.message ?: NETWORK_ERROR_MSG)
    }
  }

  @Suppress("TooGenericExceptionCaught")
  suspend fun safeDownload(
    directoryPath: String,
    fileName: String? = null,
    block: HttpRequestBuilder.() -> Unit,
  ): ApiResponse<String> = withContext(Dispatchers.IO) {
    try {
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
    } catch (e: Exception) {
      ApiResponse(null, NETWORK_ERROR, e.message ?: NETWORK_ERROR_MSG)
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

  @Suppress("TooGenericExceptionCaught")
  suspend inline fun <reified T> safeUpload(
    filePath: String,
    params: Map<String, String> = emptyMap(),
    crossinline block: HttpRequestBuilder.() -> Unit,
  ): ApiResponse<T> = withContext(Dispatchers.IO) {
    val path = Path(filePath)

    // 跨平台检查文件是否存在及获取大小
    val metadata = SystemFileSystem.metadataOrNull(path)
    if (metadata == null || !metadata.isRegularFile) {
      return@withContext ApiResponse(null, NETWORK_ERROR, "File not found or invalid: $filePath")
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

    try {
      val client = engine.httpClient
      val response = client.request { createRequestBuilder() }
      withTokenRetry(
        initialResponse = response,
        requestBlock = { client.request { createRequestBuilder() } },
        parseBlock = { it.safeParse<T>() },
      )
    } catch (e: Exception) {
      ApiResponse(null, NETWORK_ERROR, e.message ?: NETWORK_ERROR_MSG)
    }
  }
}