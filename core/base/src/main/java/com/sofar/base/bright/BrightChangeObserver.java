package com.sofar.base.bright;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.provider.Settings;
import android.util.Log;
import androidx.annotation.NonNull;

/**
 * 安卓9.0 亮度进度条非线性变化 有待调试{@link BrightnessUtils}
 */
public class BrightChangeObserver {

  private static final String TAG = "BrightChangeObserver";

  @NonNull
  Context mContext;

  public BrightChangeObserver(@NonNull Context context) {
    this.mContext = context;
  }

  public int getScreenBrightness() {
    int value = 0;
    ContentResolver cr = mContext.getContentResolver();
    try {
      value = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
    } catch (Exception e) {
      Log.d(TAG, "e=" + e.toString());
    }
    return value;
  }

  public int getBrightnessMax() {
    int maxBrightness = 255;
    try {
      Resources system = Resources.getSystem();
      int resId =
        system.getIdentifier("config_screenBrightnessSettingMaximum", "integer", "android");
      if (resId != 0) {
        if (system.getInteger(resId) > 0) {
          maxBrightness = system.getInteger(resId);
        }
      }
    } catch (Exception ignore) {
    }
    return maxBrightness;
  }

}
