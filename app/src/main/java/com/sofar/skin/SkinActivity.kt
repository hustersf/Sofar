package com.sofar.skin

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.sofar.R
import com.sofar.core.ui.util.applyEdgeToEdgeInsetsToContentRoot
import com.sofar.core.ui.util.setupEdgeToEdge
import com.sofar.skin.base.SkinBaseActivity
import com.sofar.skin.callback.ILoaderListener
import com.sofar.skin.core.SkinManager
import com.sofar.skin.core.SkinResourceManager
import com.sofar.utility.LogUtil

class SkinActivity : SkinBaseActivity() {

  companion object {
    private const val TAG = "SkinActivity"
  }

  lateinit var skinLayout1: LinearLayout
  lateinit var skinTv1: TextView

  lateinit var skinLayout2: LinearLayout
  lateinit var skinTv2: TextView
  lateinit var skinSelectorTv2: TextView

  lateinit var skinColor1: ImageView
  lateinit var skinColor2: ImageView
  lateinit var skinColor3: ImageView
  lateinit var skinColor4: ImageView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setupEdgeToEdge()
    setContentView(R.layout.skin_activity)
    applyEdgeToEdgeInsetsToContentRoot()

    skinLayout1 = findViewById(R.id.skin_background1)
    skinTv1 = findViewById(R.id.skin_text_color1)

    skinLayout2 = findViewById(R.id.skin_background2)
    skinTv2 = findViewById(R.id.skin_text_color2)
    skinSelectorTv2 = findViewById(R.id.skin_selector_text_color2)

    skinColor1 = findViewById(R.id.skin_color1)
    skinColor2 = findViewById(R.id.skin_color2)
    skinColor3 = findViewById(R.id.skin_color3)
    skinColor4 = findViewById(R.id.skin_color4)

    resourceTest()

    colorSkin()
  }

  private fun resourceTest() {
    SkinManager.get().dynamicAddView(skinLayout2, "background", R.drawable.skin_background)
    SkinManager.get().dynamicAddView(skinTv2, "textColor", R.color.main_text_color)
    SkinManager.get().dynamicAddView(skinSelectorTv2, "textColor", R.color.skin_selector_color)
  }

  private fun colorSkin() {
    // 💡 适配高版本废弃警告：用 ContextCompat 获取颜色是最标准安全的做法
    val color1 = ContextCompat.getColor(this, R.color.skin_color_1)
    val gradient1 = skinColor1.background as GradientDrawable
    gradient1.setColor(color1)
    skinColor1.setOnClickListener { view ->
      changeColorSkin(color1)
    }

    val color2 = ContextCompat.getColor(this, R.color.skin_color_2)
    val gradient2 = skinColor2.background as GradientDrawable
    gradient2.setColor(color2)
    skinColor2.setOnClickListener { view ->
      changeColorSkin(color2)
    }

    val color3 = ContextCompat.getColor(this, R.color.skin_color_3)
    val gradient3 = skinColor3.background as GradientDrawable
    gradient3.setColor(color3)
    skinColor3.setOnClickListener { view ->
      changeColorSkin(color3)
    }

    val color4 = ContextCompat.getColor(this, R.color.skin_color_4)
    val gradient4 = skinColor4.background as GradientDrawable
    gradient4.setColor(color4)
    skinColor4.setOnClickListener { view ->
      changeColorSkin(color4)
    }
  }

  private fun changeColorSkin(color: Int) {
    SkinResourceManager.get().loadColorSkin(color)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val menuInflater = menuInflater
    menuInflater.inflate(R.menu.skin_item_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // 💡 转换为 Kotlin 推荐的 when 语法替代老旧的 if-else if
    when (item.itemId) {
      R.id.skin1 -> changeSkin("red.skin")
      R.id.skin2 -> changeSkin("green.skin")
      R.id.skin3 -> changeSkin("blue.skin")
      R.id.skin4 -> SkinResourceManager.get().restoreDefaultSkin()
    }
    return super.onOptionsItemSelected(item)
  }

  private fun changeSkin(skinName: String) {
    SkinResourceManager.get().loadSkin(skinName, object : ILoaderListener {
      override fun onStart() {
      }

      override fun onSuccess() {
        LogUtil.d(TAG, "onSuccess")
      }

      override fun onFailed(errMsg: String) {
        LogUtil.d(TAG, "onFailed:$errMsg")
      }

      override fun onProgress(progress: Int) {
      }
    })
  }
}
