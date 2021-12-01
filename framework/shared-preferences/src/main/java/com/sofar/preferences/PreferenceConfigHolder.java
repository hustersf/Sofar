package com.sofar.preferences;

import java.io.File;

import android.content.Context;

import com.google.gson.Gson;

public class PreferenceConfigHolder {

  public static PreferenceConfig CONFIG;

  public interface PreferenceConfig {
    void loadLibrary(String library);

    Context getContext();

    Gson getGson();

    String getProcessName();

    File getSharedPreferencesRoot();

    void logEvent(String key, String value);
  }
}
