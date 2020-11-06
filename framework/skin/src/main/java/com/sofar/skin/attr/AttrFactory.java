package com.sofar.skin.attr;

import androidx.annotation.Nullable;

import com.sofar.skin.model.SkinAttr;
import com.sofar.skin.util.SkinL;

import java.util.HashMap;

/**
 * 构建需要换肤的属性
 */
public class AttrFactory {

  private static String TAG = "AttrFactory";

  public static HashMap<String, SkinAttr> supportAttrs = new HashMap<>();

  static {
    supportAttrs.put("background", new BackgroundAttr());
    supportAttrs.put("textColor", new TextColorAttr());
  }


  @Nullable
  public static SkinAttr get(String attrName, int attrValueRefId, String attrValueRefName, String attrTypeName) {
    SkinL.d(TAG, "attrName:" + attrName);
    SkinAttr skinAttr = supportAttrs.get(attrName);
    if (skinAttr == null) {
      return null;
    }

    skinAttr.attrName = attrName;
    skinAttr.attrValueRefId = attrValueRefId;
    skinAttr.attrValueRefName = attrValueRefName;
    skinAttr.attrValueTypeName = attrTypeName;
    return skinAttr.clone();
  }

  /**
   * 给定的属性是否支持换肤
   */
  public static boolean isSupportedAttr(String attrName) {
    return supportAttrs.containsKey(attrName);
  }

  /**
   * 自定义添加换肤属性
   */
  public static void addSupportAttr(String attrName, SkinAttr skinAttr) {
    supportAttrs.put(attrName, skinAttr);
  }

}
