package com.sofar.download;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadFactory;

public class Util {

  private static final Handler UI_HANDLER = new Handler(Looper.getMainLooper());

  static boolean isSDCardEnable() {
    return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
  }

  static File getCacheDir(@NonNull Context context) {
    // 获取保存的文件夹路径
    File file;
    if (isSDCardEnable()) {
      // 有SD卡就保存到sd卡
      file = context.getExternalCacheDir();
    } else {
      // 没有就保存到内部储存
      file = context.getCacheDir();
    }
    return file;
  }

  static boolean hasSDCardPermission(@NonNull Context context) {
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
      || ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
  }

  /**
   * 关闭io流
   */
  static void closeQuietly(Closeable closeable) {
    try {
      if (closeable != null) {
        closeable.close();
      }
    } catch (IOException ioe) {
      // ignore
    }
  }

  public static ThreadFactory threadFactory(final String name, final boolean daemon) {
    return new ThreadFactory() {
      @Override
      public Thread newThread(Runnable runnable) {
        Thread result = new Thread(runnable, name);
        result.setDaemon(daemon);
        return result;
      }
    };
  }

  public static void runOnUiThread(Runnable action) {
    if (Looper.getMainLooper() == Looper.myLooper()) {
      action.run();
    } else {
      UI_HANDLER.post(action);
    }
  }
}
