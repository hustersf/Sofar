package com.sofar.config.internal;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sofar.config.SharedPreferencesProvider;

public class ConfigStorage {
  private static final String TAG = "ConfigStorage";
  private static final String PREF_NAME = "%s_configs";

  private SharedPreferencesProvider mPreferenceProvider = Context::getSharedPreferences;
  private final SharedPreferences mSharedPreferences;
  private final Context mContext;

  public ConfigStorage(@NonNull Context context, SharedPreferencesProvider provider) {
    if (provider != null) {
      mPreferenceProvider = provider;
    }

    mContext = context;
    mSharedPreferences = mPreferenceProvider.obtain(mContext,
      String.format(PREF_NAME, mContext.getPackageName()), Context.MODE_PRIVATE);
  }

  @Nullable
  public String getValue(String key) {
    return mSharedPreferences.getString(key, null);
  }

  public void setValue(String key, String value) {
    mSharedPreferences.edit().putString(key, value).apply();
  }


  public void update(JsonObject config) {
    SharedPreferences.Editor editor = mSharedPreferences.edit();
    editor.clear();
    save(null, config, editor);
    editor.apply();
  }

  private void save(@Nullable String group, JsonObject json, SharedPreferences.Editor editor) {
    for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
      String key = entry.getKey();
      JsonElement value = entry.getValue();
      if (key == null) {
        continue;
      }
      if (value == null || value.isJsonNull()) {
        editor.remove(key);
        continue;
      }

      String k = Util.getKey(group, key);
      if (value.isJsonPrimitive()) {
        editor.putString(k, value.getAsString());
      } else {
        editor.putString(k, value.toString());
        if (value.isJsonObject()) {
          save(k, value.getAsJsonObject(), editor);
        }
      }
    }
  }
}
