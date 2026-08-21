package com.sofar

import android.app.Application
import android.content.Context
import android.util.Log
import com.getkeepsafe.relinker.ReLinker
import com.google.gson.Gson
import com.sofar.badge.BadgeNumberTreeManager
import com.sofar.base.app.AppLifeManager
import com.sofar.base.exception.SofarErrorConsumer
import com.sofar.base.location.LocationProvider
import com.sofar.config.ConfigManager
import com.sofar.download.DownloadConfig
import com.sofar.download.DownloadManager
import com.sofar.image.ImageManager
import com.sofar.network.cache.NetworkCacheInitializer
import com.sofar.preferences.PreferenceConfigHolder
import com.sofar.preferences.SofarSharedPreferences
import com.sofar.skin.core.SkinColorWhiteList
import com.sofar.skin.core.SkinManager
import com.sofar.utility.FileUtil
import com.sofar.utility.SystemUtil
import io.reactivex.plugins.RxJavaPlugins
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class SofarApp : Application() {

  private lateinit var appContext: Context

  override fun onCreate() {
    super.onCreate()
    appContext = this
    // 皮肤组件初始化
    SkinManager.get().init(this)
    SkinColorWhiteList.addSupportResName("themeColor")

    // 定位服务初始化
    LocationProvider.getInstance().init(this)

    // RxJava 全局错误拦截器处理
    RxJavaPlugins.setErrorHandler(object : SofarErrorConsumer() {
      override fun accept(t: Throwable) {
        super.accept(t)
        val sw = StringWriter(256)
        val pw = PrintWriter(sw, false)
        t.printStackTrace(pw)
        pw.flush()
        Log.d("rx_error", sw.toString())
      }
    })

    // 应用生命周期和下载、图片、红点模块初始化
    AppLifeManager.get().init(this)
    DownloadManager.get().init(this, DownloadConfig.Builder().build())
    ImageManager.get().init(this)
    BadgeNumberTreeManager.get().init(this)

    // 键值存储 Preference 全局配置初始化
    PreferenceConfigHolder.CONFIG = object : PreferenceConfigHolder.PreferenceConfig {
      override fun loadLibrary(library: String) {
        ReLinker.loadLibrary(appContext, library)
      }

      override fun getContext(): Context {
        return appContext
      }

      override fun getGson(): Gson {
        return Gson()
      }

      override fun getProcessName(): String {
        return SystemUtil.getProcessName(appContext) ?: ""
      }

      override fun getSharedPreferencesRoot(): File {
        return File(FileUtil.getDataDir(appContext), "shared_prefs")
      }

      override fun logEvent(key: String, value: String) {
        // 保持原样为空
      }
    }

    // 配置中心初始化 (使用 Kotlin 的 SAM 转换传入方法引用)
    ConfigManager.get().init(this, SofarSharedPreferences::obtain)

    // 网络缓存框架初始化 (消除 INSTANCE 后非常清爽)
    NetworkCacheInitializer.init(this)
  }
}
