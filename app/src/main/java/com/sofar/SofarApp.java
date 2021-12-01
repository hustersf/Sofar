package com.sofar;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.getkeepsafe.relinker.ReLinker;
import com.google.gson.Gson;
import com.sofar.base.app.AppLifeManager;
import com.sofar.base.exception.SofarErrorConsumer;
import com.sofar.base.location.LocationProvider;
import com.sofar.download.DownloadConfig;
import com.sofar.download.DownloadManager;
import com.sofar.fun.FunConfig;
import com.sofar.image.ImageManager;
import com.sofar.preferences.PreferenceConfigHolder;
import com.sofar.profiler.MonitorManager;
import com.sofar.skin.core.Skin;
import com.sofar.utility.FileUtil;
import com.sofar.utility.SystemUtil;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import io.reactivex.plugins.RxJavaPlugins;

public class SofarApp extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Skin.init(this);
    Skin.addSupportSkinColorResName("themeColor");

    LocationProvider.getInstance().init(this);

    FunConfig.init(this);

    RxJavaPlugins.setErrorHandler(new SofarErrorConsumer() {
      @Override
      public void accept(Throwable t) throws Exception {
        super.accept(t);
        StringWriter sw = new StringWriter(256);
        PrintWriter pw = new PrintWriter(sw, false);
        t.printStackTrace(pw);
        pw.flush();
        Log.d("rx_error", sw.toString());
      }
    });

    AppLifeManager.get().init(this);

    DownloadManager.get().init(this, new DownloadConfig.Builder().build());

    MonitorManager.init(this);
    MonitorManager.addAll();

    ImageManager.get().init(this);

    PreferenceConfigHolder.CONFIG = new PreferenceConfigHolder.PreferenceConfig() {
      @Override
      public void loadLibrary(String library) {
        ReLinker.loadLibrary(SofarApp.this, library);
      }

      @Override
      public Context getContext() {
        return SofarApp.this;
      }

      @Override
      public Gson getGson() {
        return new Gson();
      }

      @Override
      public String getProcessName() {
        return SystemUtil.getProcessName(SofarApp.this);
      }

      @Override
      public File getSharedPreferencesRoot() {
        return new File(FileUtil.getDataDir(SofarApp.this), "shared_prefs");
      }

      @Override
      public void logEvent(String key, String value) {

      }
    };
  }
}
