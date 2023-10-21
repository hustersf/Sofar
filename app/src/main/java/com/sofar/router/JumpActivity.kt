package com.sofar.router

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.sofar.base.app.AppLifeManager

class JumpActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    var uri = intent.data
    uri?.let {
      var host = it.host
      if (TextUtils.isEmpty(host)) {
        startApp(it)
      }
    }
    finish()
  }

  /**
   * h5唤醒app,短链格式[sofar://]
   */
  private fun startApp(uri: Uri) {
    var activityStackSize = AppLifeManager.get().activityStackSize
    if (activityStackSize > 1) {
      //app在后台唤醒到前台
      var key = "source"
      var value = "jump-self"
      if (!TextUtils.equals(intent.getStringExtra(key), value)) {
        var resIntent = Intent()
        resIntent.data = uri
        resIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        resIntent.putExtra(key, value)
        startActivity(resIntent)
      }
    } else {
      //app未启动
      val intent = packageManager.getLaunchIntentForPackage(packageName)
      startActivity(intent)
    }
  }

}