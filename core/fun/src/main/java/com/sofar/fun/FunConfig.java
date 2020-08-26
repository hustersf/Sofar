package com.sofar.fun;

import android.app.Application;

public class FunConfig {

  public static Application theApp;

  public static void init(Application application) {
    theApp = application;
  }

}
