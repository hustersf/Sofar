package com.sofar.kmp.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.sofar.kmp.demo.core.navigation.Navigator
import com.sofar.kmp.demo.core.navigation.rememberNavigationState
import com.sofar.kmp.demo.core.navigation.toEntries
import com.sofar.kmp.demo.feature.cache.navigation.cacheEntry
import com.sofar.kmp.demo.feature.network.navigation.networkEntry
import com.sofar.kmp.demo.navigation.MainNavKey
import com.sofar.kmp.demo.navigation.mainEntry
import com.sofar.kmp.demo.ui.theme.AppTheme

class KmpDemoMainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // 开启全屏模式
    enableEdgeToEdge()
    setContent {
      AppTheme {
        val navigationState = rememberNavigationState(startKey = MainNavKey)
        val navigator = remember { Navigator(navigationState) }

        val entryProvider = entryProvider {
          mainEntry(navigator)
          networkEntry(navigator)
          cacheEntry(navigator)
        }

        // 顶层 Scaffold 负责消费 WindowInsets (状态栏、导航栏)
        Scaffold(
          modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
          NavDisplay(
            modifier = Modifier
              .padding(innerPadding)
              .consumeWindowInsets(innerPadding), // 关键：确保内部不再重复消费 Insets
            entries = navigationState.toEntries(entryProvider),
            onBack = { navigator.goBack() }
          )
        }
      }
    }
  }
}
