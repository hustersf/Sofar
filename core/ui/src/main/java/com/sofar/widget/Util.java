package com.sofar.widget;

import java.lang.reflect.Method;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.annotation.NonNull;

public class Util {

  public static int dp2px(@NonNull Context context, float dpVal) {
    int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
      context.getResources().getDisplayMetrics());
    return value;
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

  public static int getStatusBarHeight(Context context) {
    // 获得状态栏高度
    int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
    return context.getResources().getDimensionPixelSize(resourceId);
  }

  // 获取是否存在NavigationBar
  public static boolean checkDeviceHasNavigationBar(Context context) {
    boolean hasNavigationBar = false;
    Resources rs = context.getResources();
    int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
    if (id > 0) {
      hasNavigationBar = rs.getBoolean(id);
    }
    try {
      Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
      Method m = systemPropertiesClass.getMethod("get", String.class);
      String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
      if ("1".equals(navBarOverride)) {
        hasNavigationBar = false;
      } else if ("0".equals(navBarOverride)) {
        hasNavigationBar = true;
      }
    } catch (Exception e) {

    }
    return hasNavigationBar;
  }

  public static int getNavigationBarHeight(Context context) {
    Resources resources = context.getResources();
    int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
    int height = resources.getDimensionPixelSize(resourceId);
    return height;
  }

}
