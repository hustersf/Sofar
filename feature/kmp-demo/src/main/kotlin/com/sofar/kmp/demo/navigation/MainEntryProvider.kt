package com.sofar.kmp.demo.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sofar.kmp.demo.core.navigation.Navigator
import com.sofar.kmp.demo.ui.MainMenuScreen

/**
 * 首页菜单的入口注册逻辑。
 */
fun EntryProviderScope<NavKey>.mainEntry(navigator: Navigator) {
  entry<MainNavKey> {
    MainMenuScreen(navigator = navigator)
  }
}
