package com.sofar.utility;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.annotation.NonNull;

/**
 * 获取设备信息
 */
public class DeviceUtil {

  /**
   * 获取手机型号，如MI 8
   */
  public static String getModel() {
    return Build.MODEL;
  }

  /**
   * 获取Android版本,如Android 9
   */
  public static String getOS() {
    return "Android版本" + Build.VERSION.RELEASE;
  }

  /**
   * 获取SDK版本,如28
   */
  public static int getSdkVersion() {
    return Build.VERSION.SDK_INT;
  }


  /**
   * 获取手机制造商，如Xiaomi
   */
  public static String getOC() {
    return Build.MANUFACTURER;
  }


  /**
   * 获取设备唯一id
   */
  public static String getDeviceId(@NonNull Context context) {
    if (TextUtils.isEmpty(getIMEI(context))) {
      return getUUid(context);
    } else {
      return getIMEI(context);
    }
  }

  /**
   * 获取imei 在23及以上需要动态获取权限
   */
  private static String getIMEI(@NonNull Context context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
      return tm.getDeviceId();
    } else {
      return "";
    }
  }

  /**
   * 设备唯一标示号（设备首次启动，系统随机生成的一个64位的数字，并已16进制字符串保存下来）
   * 缺陷 厂商bug
   * 1.不同的设备可能会产生相同的ANDROID_ID
   * 2.有些设备返回null
   */
  private static String getUUid(@NonNull Context context) {
    String uuid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    return uuid;
  }

  /**
   * 获取屏幕宽度px
   */
  public static int getMetricsWidth(@NonNull Context context) {
    DisplayMetrics dm = context.getResources().getDisplayMetrics();
    int screenWidth = dm.widthPixels;
    return screenWidth;
  }


  /**
   * 获取屏幕高度px
   */
  public static int getMetricsHeight(@NonNull Context context) {
    DisplayMetrics dm = context.getResources().getDisplayMetrics();
    int screenHeight = dm.heightPixels;
    return screenHeight;
  }

  /**
   * 获取手机屏幕dpi
   * mdpi{120~160}
   * hdpi{160~240}
   * xhdpi{240~320}
   * xxhdpi{320~480}
   * xxxhdpi{480~640}
   * <p>
   * 计算公式
   * px = density * dp;
   * dp = px / density;
   * density = dpi / 160;
   */
  public static int getMetricsDensityDpi(@NonNull Context context) {
    DisplayMetrics dm = context.getResources().getDisplayMetrics();
    return dm.densityDpi;
  }

  /**
   * dp和px的转化比例
   */
  public static float getMetricsDensity(@NonNull Context context) {
    DisplayMetrics dm = context.getResources().getDisplayMetrics();
    return dm.density;
  }


  public static int dp2px(@NonNull Context context, float dpVal) {
    int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources().getDisplayMetrics());
    return value;
  }

  public static int sp2px(@NonNull Context context, float spVal) {
    int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.getResources().getDisplayMetrics());
    return value;
  }

  public static float px2dp(@NonNull Context context, float pxVal) {
    float scale = context.getResources().getDisplayMetrics().density;
    return (pxVal / scale);
  }

  public static float px2sp(@NonNull Context context, float pxVal) {
    float scale = context.getResources().getDisplayMetrics().scaledDensity;
    return (pxVal / scale);
  }

}
