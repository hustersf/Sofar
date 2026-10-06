package com.sofar.kmp.demo.feature.network.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sofar.kmp.demo.core.navigation.Navigator
import com.sofar.kmp.demo.feature.network.ui.NetworkDemoScreen

fun EntryProviderScope<NavKey>.networkEntry(navigator: Navigator) {
  entry<NetworkDemoNavKey> {
    NetworkDemoScreen()
  }
}
