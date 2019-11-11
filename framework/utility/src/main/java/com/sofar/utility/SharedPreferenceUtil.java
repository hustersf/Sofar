package com.sofar.utility;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class SharedPreferenceUtil {

  private static final String SP_CONFIG = "app_config";

  public SharedPreferences preferences;

  public SharedPreferenceUtil(@NonNull Context context) {
    preferences = context.getSharedPreferences(SP_CONFIG, Context.MODE_PRIVATE);
  }

  public boolean getToggleState(String key) {
    return preferences.getBoolean(key, false);
  }

  /**
   * 存储布尔值
   */
  public void setToggleState(String key, boolean state) {
    preferences.edit().putBoolean(key, state).commit();
  }

  public String getToggleString(String key) {
    return preferences.getString(key, "");
  }

  /**
   * 存储字符串
   */
  public void setToggleString(String key, String value) {
    preferences.edit().putString(key, value).commit();
  }

  public int getToggleInt(String key) {
    return preferences.getInt(key, -1);
  }

  /**
   * 存储int类型
   */
  public void setToggleInt(String key, int value) {
    preferences.edit().putInt(key, value).commit();
  }

  public SharedPreferences.Editor getToggleEdit() {
    return preferences.edit();
  }

}
