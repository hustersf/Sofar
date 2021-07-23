package com.sofar.preloader;

import android.util.Log;

public class Logger {

  private static final String TAG = "PreLoader";

  public static void debug(String msg) {
    Log.d(TAG, msg);
  }

  public static void error(String msg) {
    Log.e(TAG, msg);
  }

  public static void warning(String msg) {
    Log.w(TAG, msg);
  }
}
