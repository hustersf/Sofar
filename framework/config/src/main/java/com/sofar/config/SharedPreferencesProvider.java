package com.sofar.config;

import android.content.Context;
import android.content.SharedPreferences;

public interface SharedPreferencesProvider {
  SharedPreferences obtain(Context context, String name, int mode);
}
