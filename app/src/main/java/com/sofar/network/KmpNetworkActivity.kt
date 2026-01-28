package com.sofar.network

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sofar.R
import com.sofar.kmp.network.core.OpenApiClient
import com.sofar.kmp.network.core.SdkConfig
import kotlinx.coroutines.launch

class KmpNetworkActivity : AppCompatActivity() {

  private lateinit var bodyLayout: View
  private lateinit var body: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setTitle("kmp网络测试页面")
    setContentView(R.layout.kmp_network_activity)

    OpenApiClient.get().init(SdkConfig.build {
      setDebugMode(true)
      setBaseUrl("https://www.wanandroid.com/")
    })

    bodyLayout = findViewById(R.id.body_layout)
    body = findViewById(R.id.body)

    bodyLayout.setOnClickListener {
      lifecycleScope.launch {
        body.text = "..."
        val result = OpenApiClient.get().banner.getBanners()
        result.fold(
          onSuccess = {
            val sb = StringBuilder()
            sb.append("size=${it.size}\n")
            it.forEach { banner ->
              sb.append(banner.title)
              sb.append("\n")
            }
            body.text = sb.toString().dropLast(1)
          },
          onFailure = {
            body.text = it.message
          }
        )
      }
    }

  }
}