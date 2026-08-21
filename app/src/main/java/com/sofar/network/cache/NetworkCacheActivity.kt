package com.sofar.network.cache

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.sofar.R
import com.sofar.core.ui.activity.BaseUIActivity
import com.sofar.network.cache.retrofit.CacheFlowCallAdapterFactory
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NetworkCacheActivity : BaseUIActivity() {

  private lateinit var sendBtn: Button
  private lateinit var logTv: TextView
  private lateinit var resultTv: TextView
  private lateinit var githubService: GithubService

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.network_cache_activity)
    initView()
    initRetrofit()
    lifecycleScope.launch {
      NetworkCacheInitializer.logFlow.collect { message ->
        log(message)
      }
    }
    sendBtn.setOnClickListener {
      val startTime = System.currentTimeMillis()
      logTv.text = ""
      resultTv.text = ""
      log("request start")
      lifecycleScope.launch {
        var receiveCount = 0
        githubService.searchRepos(
          query = "android",
          page = 1,
          itemsPerPage = 20
        ).catch {
          log("request failed:${it.message}")
        }.onCompletion {
          log("request completed")
        }.collect { response ->
          val cost = System.currentTimeMillis() - startTime
          receiveCount++
          val items = response.items
          log("receive #$receiveCount, count=${items.size}, cost=${cost}ms")
          val msg = buildString {
            appendLine("count:${items.size}")
            appendLine("first repo:${items.firstOrNull()?.name}")
          }
          resultTv.text = msg
        }
      }
    }
  }

  private fun initView() {
    sendBtn = findViewById(R.id.send_btn)
    resultTv = findViewById(R.id.result_tv)
    logTv = findViewById(R.id.log_tv)
  }

  private fun initRetrofit() {
    val retrofit = Retrofit.Builder()
      .baseUrl("https://api.github.com/")
      .client(createOkHttpClient())
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(CacheFlowCallAdapterFactory.create())
      .build()
    githubService = retrofit.create(GithubService::class.java)
  }

  private fun createOkHttpClient(): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    return OkHttpClient.Builder()
      .addInterceptor(loggingInterceptor)
      .build()
  }

  private fun log(message: String) {
    runOnUiThread {
      val time = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
      logTv.append("[$time] $message\n")
      Log.d("NetworkCacheActivity", "$message")
    }
  }
}
