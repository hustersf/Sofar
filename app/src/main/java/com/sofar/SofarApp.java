package com.sofar;

import android.app.Application;

import com.sofar.skin.core.Skin;

public class SofarApp extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Skin.init(this);
  }
}
