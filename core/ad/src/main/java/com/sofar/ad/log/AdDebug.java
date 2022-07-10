package com.sofar.ad.log;

import android.util.Log;

public class AdDebug {

  private static String LOG_TAG = "SofarAd";
  private static boolean sEnableDebug = true;

  public static void setEnableDebug(boolean enableDebug) {
    sEnableDebug = enableDebug;
  }

  public static void d(String msg, Object... args) {
    if (sEnableDebug) {
      Log.d(LOG_TAG, format(msg, args));
    }
  }

  public static void i(String msg, Object... args) {
    if (sEnableDebug) {
      Log.i(LOG_TAG, format(msg, args));
    }
  }

  public static void w(String msg, Object... args) {
    if (sEnableDebug) {
      Log.w(LOG_TAG, format(msg, args));
    }
  }

  public static void w(Throwable t) {
    if (sEnableDebug) {
      Log.w(LOG_TAG, t);
    }
  }

  public static void e(String msg, Object... args) {
    if (sEnableDebug) {
      Log.e(LOG_TAG, format(msg, args));
    }
  }

  public static void e(Throwable t) {
    if (sEnableDebug) {
      Log.e(LOG_TAG, "", t);
    }
  }


  private static String format(String msg, Object... args) {
    if (args != null && args.length > 0) {
      try {
        return String.format(msg, args);
      } catch (Throwable t) {

      }
    }
    return msg;
  }
}
