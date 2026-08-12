package com.sofar.core.ui.activity

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import com.sofar.core.ui.util.applyEdgeToEdgeInsetsToContentRoot
import com.sofar.core.ui.util.setupEdgeToEdge

/**
 * 应用级 UI 基类。
 *
 * 提供 Activity 通用 UI 能力与统一行为。
 * 新增能力前请评估其通用性和归属，避免职责膨胀。
 */
abstract class BaseUIActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setupEdgeToEdge()
  }

  override fun setContentView(layoutResID: Int) {
    super.setContentView(layoutResID)
    applyInsetsToRoot()
  }

  override fun setContentView(view: View?) {
    super.setContentView(view)
    applyInsetsToRoot()
  }

  override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
    super.setContentView(view, params)
    applyInsetsToRoot()
  }

  private fun applyInsetsToRoot() {
    applyEdgeToEdgeInsetsToContentRoot { windowInsetsType() }
  }

  open fun windowInsetsType(): Int {
    return WindowInsetsCompat.Type.systemBars()
  }
}