package com.sofar.skin.core;

import android.app.Application;

import androidx.annotation.NonNull;

import com.sofar.skin.attr.AttrFactory;
import com.sofar.skin.model.SkinAttr;

/**
 * 对外提供皮肤调用的接口
 */
public class Skin {

  /**
   * Application 继承和init二选一
   * Activity暂时只支持继承的方式
   */
  public static void init(@NonNull Application application) {
    SkinManager.getInstance().init(application);
  }

  /**
   * 添加自定义属性
   */
  public static void addSupportAttr(String attrName, SkinAttr skinAttr) {
    AttrFactory.addSupportAttr(attrName, skinAttr);
  }

  /**
   * 添加需要支持纯色换肤的资源的名字
   */
  public static void addSupportSkinColorResName(String resName) {
    SkinColorWhiteList.addSupportResName(resName);
  }
}
