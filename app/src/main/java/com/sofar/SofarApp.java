package com.sofar;

import android.app.Application;
import android.util.Log;

import com.sofar.base.app.AppLifeManager;
import com.sofar.base.exception.SofarErrorConsumer;
import com.sofar.base.location.LocationProvider;
import com.sofar.download.DownloadConfig;
import com.sofar.download.DownloadManager;
import com.sofar.fun.FunConfig;
import com.sofar.profiler.MonitorManager;
import com.sofar.skin.core.Skin;

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
  }
}
