package com.sofar.kmp.demo.feature.network.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sofar.kmp.demo.feature.network.service.HttpBinResponse
import com.sofar.kmp.demo.feature.network.vm.NetworkDemoViewModel
import com.sofar.kmp.network.openapi.api.model.Banner

@Composable
fun NetworkDemoScreen(
  viewModel: NetworkDemoViewModel = viewModel()
) {
  val logs by viewModel.logs.collectAsStateWithLifecycle()
  val bannerResult by viewModel.bannerResult.collectAsStateWithLifecycle()
  val httpBinResult by viewModel.httpBinResult.collectAsStateWithLifecycle()
  val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
  val listState = rememberLazyListState()

  // 自动滚动到底部
  LaunchedEffect(logs.size) {
    if (logs.isNotEmpty()) {
      listState.animateScrollToItem(logs.size - 1)
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    bottomBar = {
      BottomActionButtons(
        onGetClick = { viewModel.fetchBanners() },
        onPostClick = { viewModel.testPostRequest() }
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .padding(innerPadding)
        .fillMaxSize()
        .padding(horizontal = 16.dp)
    ) {
      Text(
        text = "KMP Network Demo",
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(vertical = 12.dp)
      )

      // 1. 结果展示区域
      ResultDisplayCard(
        bannerResult = bannerResult,
        httpBinResult = httpBinResult,
        errorMessage = errorMessage
      )

      Spacer(modifier = Modifier.height(16.dp))

      // 2. 日志打印区域
      ConsoleLogList(logs = logs, listState = listState)
    }
  }
}

@Composable
private fun BottomActionButtons(
  onGetClick: () -> Unit,
  onPostClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(16.dp)
  ) {
    Button(onClick = onGetClick, modifier = Modifier.fillMaxWidth()) {
      Text(text = "GET Request (WanAndroid)")
    }
    Spacer(modifier = Modifier.height(8.dp))
    Button(onClick = onPostClick, modifier = Modifier.fillMaxWidth()) {
      Text(text = "POST Request (HttpBin)")
    }
  }
}

@Composable
private fun ResultDisplayCard(
  bannerResult: List<Banner>?,
  httpBinResult: HttpBinResponse?,
  errorMessage: String?,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .height(120.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant
    )
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Text(text = "Search Result:", style = MaterialTheme.typography.labelMedium)
      when {
        bannerResult != null -> {
          Text(text = "Total Banners: ${bannerResult.size}")
          Text(
            text = "First: ${bannerResult.firstOrNull()?.title}",
            color = MaterialTheme.colorScheme.primary
          )
        }

        httpBinResult != null -> {
          Text(
            text = "URL: ${httpBinResult.url}",
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = "Data: ${httpBinResult.json?.content}",
            color = MaterialTheme.colorScheme.primary
          )
        }

        errorMessage != null -> {
          Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error
          )
        }

        else -> {
          Text(text = "No data yet", color = Color.Gray)
        }
      }
    }
  }
}

@Composable
private fun ConsoleLogList(
  logs: List<String>,
  listState: LazyListState,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier) {
    Text(text = "Console Logs:", style = MaterialTheme.typography.labelMedium)
    LazyColumn(
      state = listState,
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .background(Color.Black.copy(alpha = 0.05f))
        .padding(8.dp)
    ) {
      items(logs) { log ->
        val isError = log.contains("catch") || log.contains("error")
        Text(
          text = log,
          fontSize = 12.sp,
          fontFamily = FontFamily.Monospace,
          color = if (isError) Color.Red else Color.Unspecified
        )
      }
    }
  }
}
