package com.sofar.kmp.demo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = true,
  content: @Composable() () -> Unit
) {
  BaseAppTheme(
    darkTheme = darkTheme,
    dynamicColor = dynamicColor,
    content = content
  )
}