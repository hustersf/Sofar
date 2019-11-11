package com.sofar.utility;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class ToastUtil {

  public static void startShort(@NonNull Context context, String text) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
  }

  public static void startLong(@NonNull Context context, String text) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show();
  }
}
