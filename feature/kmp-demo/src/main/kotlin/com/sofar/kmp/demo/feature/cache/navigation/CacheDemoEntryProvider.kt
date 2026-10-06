package com.sofar.kmp.demo.feature.cache.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sofar.kmp.demo.core.navigation.Navigator
import com.sofar.kmp.demo.feature.cache.ui.CacheDemoScreen

fun EntryProviderScope<NavKey>.cacheEntry(navigator: Navigator) {
  entry<CacheDemoNavKey> {
    CacheDemoScreen()
  }
}
