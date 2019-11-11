package com.sofar.utility;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.Locale;

public class LanguageUtil {

  public static String getCurrentLanguage(@NonNull Context context) {
    String language = context.getApplicationContext().getResources().getConfiguration().locale.getLanguage();
    if (TextUtils.isEmpty(language)) {
      language = Locale.getDefault().getLanguage();
    }
    return language;

  }

  public static String getCurrentCountry(@NonNull Context context) {
    String country = context.getApplicationContext().getResources().getConfiguration().locale.getCountry();
    if (TextUtils.isEmpty(country)) {
      country = Locale.getDefault().getCountry();
    }
    return country;
  }

}
