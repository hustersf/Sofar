package com.sofar.skin.core;

import java.util.ArrayList;
import java.util.List;

/**
 * 存储那些支持纯颜色换肤的资源名字
 */
public class SkinColorWhiteList {

  public static List<String> supportSkinColorResNames = new ArrayList<>();

  public static boolean isSupportResName(String resName) {
    return supportSkinColorResNames.contains(resName);
  }

  public static void addSupportResName(String resName) {
    supportSkinColorResNames.add(resName);
  }

}
