package com.sofar.kmp.demo.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator

/**
 * 导航操作器：负责具体的进栈、出栈指令。
 */
class Navigator(val state: NavigationState) {
  fun navigate(key: NavKey) {
    state.stack.apply {
      remove(key)
      add(key)
    }
  }

  fun goBack() {
    state.stack.apply {
      if (size > 1) removeAt(size - 1)
    }
  }
}

/**
 * 导航状态：管理单一回退栈。
 */
class NavigationState(val stack: NavBackStack<NavKey>)

@Composable
fun rememberNavigationState(startKey: NavKey): NavigationState {
  val stack = rememberNavBackStack(startKey)
  return remember { NavigationState(stack) }
}

/**
 * 将逻辑栈转换为渲染条目。
 */
@Composable
fun NavigationState.toEntries(
  entryProvider: (NavKey) -> NavEntry<NavKey>,
): List<NavEntry<NavKey>> {
  val decorators = listOf(
    rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
    rememberViewModelStoreNavEntryDecorator<NavKey>()
  )
  return rememberDecoratedNavEntries(
    backStack = stack,
    entryDecorators = decorators,
    entryProvider = entryProvider,
  )
}
