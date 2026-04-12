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
      setBaseUrl("https://wanandroid.com/")
    })

    bodyLayout = findViewById(R.id.body_layout)
    body = findViewById(R.id.body)

    bodyLayout.setOnClickListener {
      lifecycleScope.launch {
        body.text = "..."
        val response = OpenApiClient.get().banner.getBanners()
        if (response.isSuccess && response.data != null) {
          val data = response.data!!
          val sb = StringBuilder()
          sb.append("size=${data.size}\n")
          data.forEach { banner ->
            sb.append(banner.title)
            sb.append("\n")
          }
          body.text = sb.toString().dropLast(1)
        } else {
          body.text = "${response.errorCode}:${response.errorMsg}"
        }
      }
    }

  }
}