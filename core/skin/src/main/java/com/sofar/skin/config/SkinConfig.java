package com.sofar.skin.config;

import android.content.Context;

import com.sofar.skin.util.SkinPreferenceUtil;

/**
 * 换肤相关配置
 */
public class SkinConfig {

  public static final String NAMESPACE = "http://schemas.android.com/skin";
  public static final String ATTR_SKIN_ENABLE = "enable";

  public static final String SKIN_DIR_NAME = "skin";

  public static final String PREF_SKIN_NAME = "skin_name";

  /**
   * 获取使用的皮肤包名,未使用皮肤则返回空
   */
  public static String getSkinName(Context context) {
    SkinPreferenceUtil util = new SkinPreferenceUtil(context);
    return util.getToggleString(PREF_SKIN_NAME);
  }

  /**
   * 保存本地使用的皮肤包的名字
   */
  public static void saveSkinName(Context context, String skinName) {
    SkinPreferenceUtil util = new SkinPreferenceUtil(context);
    util.setToggleString(PREF_SKIN_NAME, skinName);
  }

}
