package com.sofar.core.ui.util

import android.R
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

private const val DEFAULT_CONTENT_ROOT_ID = R.id.content

/**
 * 为 Activity 开启沉浸式布局。
 * Enable edge-to-edge layout for an Activity.
 */
fun AppCompatActivity.setupEdgeToEdge() {
  enableEdgeToEdge()
}

/**
 * 为 Activity 内容根视图应用 WindowInsets padding，可用于组合复用。
 * Apply WindowInsets padding to the Activity content root for composition-based reuse.
 */
fun Activity.applyEdgeToEdgeInsetsToContentRoot(
  @IdRes contentRootId: Int = DEFAULT_CONTENT_ROOT_ID,
  insetTypes: () -> Int = { WindowInsetsCompat.Type.systemBars() }
) {
  val contentContainer = findViewById<ViewGroup>(contentRootId) ?: return
  val rootView = contentContainer.getChildAt(0) ?: return
  rootView.applyEdgeToEdgeInsetsPadding(insetTypes)
}

/**
 * 为指定 View 应用 WindowInsets padding，可用于更细粒度的组合复用。
 * Apply WindowInsets padding to a target View for fine-grained composition reuse.
 */
fun View.applyEdgeToEdgeInsetsPadding(
  insetTypes: () -> Int = { WindowInsetsCompat.Type.systemBars() }
) {
  ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
    val systemBars = insets.getInsets(insetTypes())
    view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
    insets
  }
  ViewCompat.requestApplyInsets(this)
}
