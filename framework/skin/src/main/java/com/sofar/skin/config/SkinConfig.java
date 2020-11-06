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

  //纯颜色换肤相关
  public static final String SKIN_COLOR_NAME = "skin_color";
  public static final String KEY_SKIN_COLOR_VALUE = "key_skin_color_value";
  public static int SKIN_COLOR_VALUE = -1;

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

  /**
   * 获取纯色换肤的颜色值
   */
  public static int getSkinColorValue(Context context) {
    SkinPreferenceUtil util = new SkinPreferenceUtil(context);
    return util.getToggleInt(KEY_SKIN_COLOR_VALUE);
  }

  /**
   * 保存使用的纯色皮肤的颜色值
   */
  public static void saveSkinColorValue(Context context, int color) {
    SkinPreferenceUtil util = new SkinPreferenceUtil(context);
    util.setToggleInt(KEY_SKIN_COLOR_VALUE, color);
  }


}
