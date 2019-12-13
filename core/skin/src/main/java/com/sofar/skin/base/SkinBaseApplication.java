package com.sofar.skin.base;

import android.app.Application;

import com.sofar.skin.core.SkinManager;

public class SkinBaseApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    SkinManager.getInstance().init(this);
  }
}
