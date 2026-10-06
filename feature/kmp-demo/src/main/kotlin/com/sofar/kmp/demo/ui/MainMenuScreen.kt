package com.sofar.kmp.demo.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.sofar.kmp.demo.core.navigation.Navigator
import com.sofar.kmp.demo.feature.cache.navigation.CacheDemoNavKey
import com.sofar.kmp.demo.feature.network.navigation.NetworkDemoNavKey

// 1. 定义功能项的数据结构
data class LibraryFeature(
  val title: String,
  val description: String,
  val destination: NavKey
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(navigator: Navigator) {
  // 2. 准备演示库的列表数据（请根据你实际的 Screen 类名替换 destination 字段）
  val features = listOf(
    LibraryFeature(
      title = "network-ktor",
      description = "演示基于 ktor 的网络库的使用",
      destination = NetworkDemoNavKey
    ),
    LibraryFeature(
      title = "network-cache",
      description = "演示基于 network-ktor 的网络缓存库的使用",
      destination = CacheDemoNavKey
    )
  )

  // 3. 构建界面布局
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("kmp库功能演示") },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer,
          titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
      )
    }
  ) { innerPadding ->
    // 使用 LazyColumn 渲染高效列表
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(features) { feature ->
        LibraryFeatureItem(feature = feature) {
          // 4. 触发导航跳转
          navigator.navigate(feature.destination)
        }
      }
    }
  }
}

// 5. 抽取可复用的列表卡片组件
@Composable
fun LibraryFeatureItem(
  feature: LibraryFeature,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }, // 使得整张卡片可点击
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // 左侧文本说明区
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = feature.title,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = feature.description,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
      }

      // 右侧导航箭头
      Icon(
        imageVector = Icons.Default.ArrowForward,
        contentDescription = "进入演示",
        tint = MaterialTheme.colorScheme.primary
      )
    }
  }
}
