package com.sofar.kmp.demo.feature.network.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sofar.kmp.demo.feature.network.service.HttpBinResponse
import com.sofar.kmp.demo.feature.network.service.HttpBinService
import com.sofar.kmp.demo.feature.network.service.TestRequest
import com.sofar.kmp.network.openapi.OpenApiClient
import com.sofar.kmp.network.openapi.SdkConfig
import com.sofar.kmp.network.openapi.api.model.Banner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.getValue

/**
 * 网络功能演示 ViewModel
 */
class NetworkDemoViewModel : ViewModel() {

  private val _logs = MutableStateFlow<List<String>>(emptyList())
  val logs: StateFlow<List<String>> = _logs.asStateFlow()

  private val _bannerResult = MutableStateFlow<List<Banner>?>(null)
  val bannerResult: StateFlow<List<Banner>?> = _bannerResult.asStateFlow()

  private val _httpBinResult = MutableStateFlow<HttpBinResponse?>(null)
  val httpBinResult: StateFlow<HttpBinResponse?> = _httpBinResult.asStateFlow()

  private val _errorMessage = MutableStateFlow<String?>(null)
  val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

  private val apiClient by lazy {
    OpenApiClient(SdkConfig.build {
      setDebugMode(true)
      setBaseUrl("https://wanandroid.com/")
    })
  }

  private val httpBinService by lazy {
    HttpBinService()
  }

  fun fetchBanners() {
    val startTime = System.currentTimeMillis()
    _logs.value = emptyList()
    _bannerResult.value = null
    _httpBinResult.value = null
    _errorMessage.value = null
    log("request start")

    viewModelScope.launch {
      var receiveCount = 0
      flow {
        emit(apiClient.banner.getBanners())
      }.onEach {
        log("request onEach")
      }.catch {
        log("request catch: ${it.message}")
        _errorMessage.value = "Failed: ${it.message}"
      }.onCompletion {
        log("request onCompletion")
      }.collect { response ->
        log("request collect")
        val cost = System.currentTimeMillis() - startTime
        receiveCount++
        if (response.isSuccess && response.data != null) {
          val banners = response.data!!
          log("receive #$receiveCount, count=${banners.size}, cost=${cost}ms")
          _bannerResult.value = banners
        } else {
          log("request error: code=${response.errorCode}, msg=${response.errorMsg}")
          _errorMessage.value = "Error: ${response.errorCode} - ${response.errorMsg}"
        }
      }
    }
  }

  fun testPostRequest() {
    val startTime = System.currentTimeMillis()
    _logs.value = emptyList()
    _bannerResult.value = null
    _httpBinResult.value = null
    _errorMessage.value = null
    log("POST request start")

    viewModelScope.launch {
      var receiveCount = 0
      val body = TestRequest(id = "123", content = "Hello KMP Network")
      httpBinService.testPost(body)
        .onEach {
          log("POST request onEach")
        }.catch {
          log("POST request catch: ${it.message}")
          _errorMessage.value = "Failed: ${it.message}"
        }.onCompletion {
          log("POST request onCompletion")
        }.collect { response ->
          log("POST request collect")
          val cost = System.currentTimeMillis() - startTime
          receiveCount++
          log("receive #$receiveCount, url=${response.url}, cost=${cost}ms")
          _httpBinResult.value = response
        }
    }
  }

  private fun log(message: String) {
    val time = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
    _logs.update { it + "[$time] $message" }
  }
}
