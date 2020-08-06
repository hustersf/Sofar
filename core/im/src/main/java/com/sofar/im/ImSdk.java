package com.sofar.im;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class ImSdk {

  private static final AtomicBoolean sInit = new AtomicBoolean(false);


  public synchronized static void init(@NonNull Context context, @NonNull ImConfig config) {
    if (!sInit.get()) {

      sInit.set(true);
    }
  }

}
