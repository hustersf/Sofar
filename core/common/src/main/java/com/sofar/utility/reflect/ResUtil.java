package com.sofar.utility.reflect;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * 安卓通过反射获取资源id
 */
public class ResUtil {

  public static int getLayoutId(@NonNull Context paramContext, String paramString) {
    return paramContext.getResources().getIdentifier(paramString, "layout",
      paramContext.getPackageName());
  }

  public static int getStringId(@NonNull Context paramContext, String paramString) {
    return paramContext.getResources().getIdentifier(paramString, "string",
      paramContext.getPackageName());
  }

  public static int getDrawableId(@NonNull Context paramContext, String paramString) {
    return paramContext.getResources().getIdentifier(paramString,
      "drawable", paramContext.getPackageName());
  }

  public static int getMipmapId(@NonNull Context paramContext, String paramString) {
    return paramContext.getResources().getIdentifier(paramString,
      "mipmap", paramContext.getPackageName());
  }


  public static int getStyleId(@NonNull Context paramContext, String paramString) {
    return paramContext.getResources().getIdentifier(paramString,
      "style", paramContext.getPackageName());
  }

  public static int getId(@NonNull Context paramContext, String paramString) {
    return paramContext.getResources().getIdentifier(paramString, "id",
      paramContext.getPackageName());
  }

  public static int getColorId(@NonNull Context paramContext, String paramString) {
    return paramContext.getResources().getIdentifier(paramString,
      "color", paramContext.getPackageName());
  }

  public static int getArrayId(@NonNull Context paramContext, String paramString) {
    return paramContext.getResources().getIdentifier(paramString,
      "array", paramContext.getPackageName());
  }

}
