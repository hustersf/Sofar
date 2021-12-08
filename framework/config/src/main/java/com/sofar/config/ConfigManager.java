package com.sofar.config;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sofar.config.internal.ConfigStorage;
import com.sofar.config.internal.IConfig;
import com.sofar.config.internal.Primitives;
import com.sofar.config.internal.Util;

public class ConfigManager implements IConfig {
  private static final String TAG = "ConfigManager";

  private ConfigStorage mStorage;
  private Gson mGson;

  private ConfigManager() {
  }

  private static class Inner {
    private static ConfigManager INSTANCE = new ConfigManager();
  }

  public static ConfigManager get() {
    return Inner.INSTANCE;
  }

  public void init(@NonNull Context context, SharedPreferencesProvider provider) {
    mStorage = new ConfigStorage(context, provider);
    mGson = new Gson();
  }

  @Override
  public <T> T getValue(String key, Class<T> classOfT, T defaultValue) {
    String value = mStorage.getValue(key);
    if (value == null) {
      return defaultValue;
    }

    try {
      if (classOfT.isAssignableFrom(value.getClass())) {
        return (T) value;
      }
      return mGson.fromJson(value, classOfT);
    } catch (Exception e) {
      Log.d(TAG, "key=" + key + " getValue e:" + e.toString());
      return defaultValue;
    }
  }

  @Override
  public <T> void setValue(String key, Class<T> classOfT, T value) {
    if (Primitives.isConfigPrimitive(classOfT)) {
      mStorage.setValue(key, String.valueOf(value));
    } else {
      mStorage.setValue(key, mGson.toJson(value));
    }
  }

  /**
   * 多层级配置时key的规则
   */
  public String getKey(String... key) {
    return Util.getKey(key);
  }

  public void update(JsonObject jsonObject) {
    mStorage.update(jsonObject);
  }

}
