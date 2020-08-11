package com.sofar.widget;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.annotation.NonNull;

public class Util {

  public static int dp2px(@NonNull Context context, float dpVal) {
    int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources().getDisplayMetrics());
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

}
