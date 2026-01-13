package com.sofar.network

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sofar.R
import com.sofar.network2.api.ApiService
import com.sofar.network2.api.execute
import com.sofar.network2.core.OpenApiClient
import com.sofar.network2.core.SdkConfig
import com.sofar.network2.core.on
import kotlinx.coroutines.launch

class Network2Activity : AppCompatActivity() {

  private lateinit var bodyLayout: View
  private lateinit var dataLayout: View
  private lateinit var body: TextView
  private lateinit var data: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setTitle("网络测试页面2")
    setContentView(R.layout.network_activity)

    OpenApiClient.get().init(this, SdkConfig.build {
      setDebugMode(true)
      setBaseUrl("http://www.wanandroid.com/")
    })

    bodyLayout = findViewById(R.id.body_layout)
    dataLayout = findViewById(R.id.data_layout)
    body = findViewById(R.id.body)
    data = findViewById(R.id.data)

    bodyLayout.setOnClickListener {
      lifecycleScope.launch {
        body.text = "..."
        val result = OpenApiClient.get().apiService.getBannerDataResponse()
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
        val result = OpenApiClient.get().on<ApiService>().execute {
          getBannerData()
        }
        // 方式2unwrap() 链式
        // val result = OpenApiClient.get().apiService.getBannerData().unwrap()

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