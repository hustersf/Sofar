package com.sofar.utility;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 获取app相关信息
 */
public class AppUtil {

  /**
   * 跳转应用市场
   */
  public static void goMarket(@NonNull Context context) {
    final Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }

  /**
   * 获取应用程序包名
   */
  public static String getPackageName(@NonNull Context context) {
    return context.getPackageName();
  }

  /**
   * 获取应用程序图标
   */
  @Nullable
  public Drawable getAppIcon(@NonNull Context context) {
    try {
      // 包管理操作管理类
      PackageManager pm = context.getPackageManager();
      // 获取到应用信息
      ApplicationInfo info = pm.getApplicationInfo(context.getPackageName(), 0);
      return info.loadIcon(pm);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 获取应用程序名称
   */
  public static String getAppName(@NonNull Context context) {
    try {
      PackageManager packageManager = context.getPackageManager();
      PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
      int labelRes = packageInfo.applicationInfo.labelRes;
      return context.getResources().getString(labelRes);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return "";
  }

  /**
   * 获取应用程序版本名称信息
   */
  public static String getVersionName(@NonNull Context context) {
    try {
      PackageManager packageManager = context.getPackageManager();
      PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
      return packageInfo.versionName;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return "";
  }

  /**
   * 获取应用程序版本号
   */
  public static long getVersionCode(@NonNull Context context) {
    long versionCode = 0;
    try {
      // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
      PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
      versionCode = packageInfo.getLongVersionCode();
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return versionCode;
  }


}
