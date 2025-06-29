package com.sofar.skin.core;

import java.util.List;

import android.content.Context;
import android.view.View;
import androidx.annotation.NonNull;

import com.sofar.skin.attr.AttrFactory;
import com.sofar.skin.model.DynamicAttr;
import com.sofar.skin.model.SkinAttr;

/**
 * 对外提供调用接口
 * <p>
 * app 支持换肤有三种方式, 任选一种即可
 * 1.app 注册 {@link SkinLifecycleObserve}
 * 2.activity 继承自 {@link com.sofar.skin.base.SkinBaseActivity}
 * 3.仿照 SkinBaseActivity 直接使用 {@link SkinCompatDelegate}
 */
public class SkinManager {

  private static class HolderClass {
    private static SkinManager INSTANCE = new SkinManager();
  }

  public static SkinManager get() {
    return HolderClass.INSTANCE;
  }

  public void init(@NonNull Context context) {
    SkinResourceManager.get().init(context);
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

  /**
   * 恢复至默认皮肤
   */
  public void restoreDefaultSkin() {
    SkinResourceManager.get().restoreDefaultSkin();
  }

  /**
   * 非 xml 中的 view 支持换肤
   */
  public void dynamicAddView(@NonNull View view, List<DynamicAttr> dynamicAttrs) {
    SkinObserverManager.get().dynamicAddView(view, dynamicAttrs);
  }

  /**
   * 非 xml 中的 view 支持换肤
   */
  public void dynamicAddView(@NonNull View view, String attrName, int attrValueResId) {
    SkinObserverManager.get().dynamicAddView(view, attrName, attrValueResId);
  }

}
