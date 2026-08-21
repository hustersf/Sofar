package com.sofar.network

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.sofar.R
import com.sofar.core.ui.activity.BaseUIActivity
import com.sofar.network.openapi.OpenApiClient
import com.sofar.network.openapi.SdkConfig
import com.sofar.network.openapi.api.ApiService
import com.sofar.network.openapi.execute
import com.sofar.network.openapi.on
import kotlinx.coroutines.launch

class Network2Activity : BaseUIActivity() {

  private lateinit var bodyLayout: View
  private lateinit var dataLayout: View
  private lateinit var body: TextView
  private lateinit var data: TextView

  val apiClient: OpenApiClient by lazy {
    val config = SdkConfig.build {
      setDebugMode(true)
      setBaseUrl("https://wanandroid.com/")
    }
    OpenApiClient(this, config)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setTitle("网络测试页面2")
    setContentView(R.layout.network_activity)

    bodyLayout = findViewById(R.id.body_layout)
    dataLayout = findViewById(R.id.data_layout)
    body = findViewById(R.id.body)
    data = findViewById(R.id.data)

    bodyLayout.setOnClickListener {
      lifecycleScope.launch {
        body.text = "..."
        val result = apiClient.apiService.getBannerDataResponse()
        result.fold(
          onSuccess = {
            body.text = it
          },
          onFailure = {
            body.text = it.message
          }
        )
      }
    }

    dataLayout.setOnClickListener {
      lifecycleScope.launch {
        data.text = "..."
        // 方式1：execute { ... } 闭包式
        val result = apiClient.on<ApiService>().execute {
          getBannerData()
        }
        // 方式2unwrap() 链式
        // val result = apiClient.apiService.getBannerData().unwrap()

        result.fold(
          onSuccess = {
            val sb = StringBuilder()
            sb.append("size=${it.size}\n")
            it.forEach { banner ->
              sb.append(banner.title)
              sb.append("\n")
            }
            data.text = sb.toString().dropLast(1)
          },
          onFailure = {
            data.text = "${it.javaClass.simpleName}\n${it.message}"
          }
        )
      }
    }
  }
}