package com.sofar.skin.core;

import android.app.Application;

import androidx.annotation.NonNull;

/**
 * 对外提供皮肤调用的接口
 */
public class Skin {

  /**
   * Application 继承和init二选一
   * Activity暂时只支持继承的方式
   */
  public void init(@NonNull Application application) {
    SkinManager.getInstance().init(application);
  }
}
