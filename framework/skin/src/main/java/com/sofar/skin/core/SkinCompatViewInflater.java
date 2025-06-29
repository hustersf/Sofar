package com.sofar.skin.core;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.sofar.skin.attr.AttrFactory;
import com.sofar.skin.config.SkinConfig;
import com.sofar.skin.model.DynamicAttr;
import com.sofar.skin.model.SkinAttr;
import com.sofar.skin.model.SkinInfo;
import com.sofar.skin.util.SkinCollectionUtil;
import com.sofar.skin.util.SkinL;

/**
 * 采集 需要换肤的 view
 */
public class SkinCompatViewInflater {

  private List<SkinInfo> mSkinInfoList = new ArrayList<>();

  public View createView(@NonNull AppCompatDelegate delegate, @Nullable View parent,
    final String name, @NonNull Context context, @NonNull AttributeSet attrs) {
    View view = delegate.createView(parent, name, context, attrs);

    boolean skinEnable = attrs.getAttributeBooleanValue(SkinConfig.NAMESPACE,
      SkinConfig.ATTR_SKIN_ENABLE, false);
    if (skinEnable && view != null) {
      parseSkinView(context, attrs, view);
    }
    return view;
  }

  private void parseSkinView(@NonNull Context context, @NonNull AttributeSet attrs,
    @NonNull View view) {
    List<SkinAttr> viewAttrs = parseSkinAttrs(context, attrs);
    if (!SkinCollectionUtil.isEmpty(viewAttrs)) {
      SkinInfo skinInfo = new SkinInfo();
      skinInfo.view = view;
      skinInfo.skinAttrs = viewAttrs;
      mSkinInfoList.add(skinInfo);
      if (SkinResourceManager.get().isExternalSkin()) {
        skinInfo.apply();
      }
    }
  }

  private List<SkinAttr> parseSkinAttrs(@NonNull Context context, @NonNull AttributeSet attrs) {
    List<SkinAttr> viewAttrs = new ArrayList<>();
    //遍历当前View的属性
    for (int i = 0; i < attrs.getAttributeCount(); i++) {
      String attrName = attrs.getAttributeName(i);//属性名
      String attrValue = attrs.getAttributeValue(i);//属性值

      if (!AttrFactory.isSupportedAttr(attrName)) {
        continue;
      }

      //也就是引用类型，形如@color/red
      if (attrValue.startsWith("@")) {
        try {
          //资源id
          int id = Integer.valueOf(attrValue.substring(1));
          SkinAttr skinAttr = buildSkinAttr(context, attrName, id);
          if (skinAttr != null) {
            viewAttrs.add(skinAttr);
            SkinL.d(skinAttr.toString());
          }
        } catch (Exception e) {
          if (e != null) {
            SkinL.d("parseSkinAttr failed:" + e.toString());
          }
        }
      }
    }
    return viewAttrs;
  }

  public void dynamicAddSkinView(@NonNull View view, @NonNull List<DynamicAttr> dynamicAttrs) {
    List<SkinAttr> viewAttrs = new ArrayList<>();
    for (DynamicAttr attr : dynamicAttrs) {
      SkinAttr skinAttr = buildSkinAttr(view.getContext(), attr.attrName, attr.refResId);
      if (skinAttr != null) {
        viewAttrs.add(skinAttr);
      }
    }
    SkinInfo skinInfo = new SkinInfo();
    skinInfo.view = view;
    skinInfo.skinAttrs = viewAttrs;
    mSkinInfoList.add(skinInfo);
    skinInfo.apply();
  }

  public void dynamicAddSkinView(@NonNull View view, String attrName, int attrValueResId) {
    if (!AttrFactory.isSupportedAttr(attrName)) {
      return;
    }

    List<SkinAttr> viewAttrs = new ArrayList<>();
    SkinAttr skinAttr = buildSkinAttr(view.getContext(), attrName, attrValueResId);
    if (skinAttr != null) {
      viewAttrs.add(skinAttr);
      SkinInfo skinInfo = new SkinInfo();
      skinInfo.view = view;
      skinInfo.skinAttrs = viewAttrs;
      mSkinInfoList.add(skinInfo);
      skinInfo.apply();
    }
  }

  @Nullable
  private SkinAttr buildSkinAttr(@NonNull Context context, String attrName, int attrValueResId) {
    String name = context.getResources().getResourceEntryName(attrValueResId);
    String type = context.getResources().getResourceTypeName(attrValueResId);
    SkinAttr skinAttr = AttrFactory.get(attrName, attrValueResId, name, type);
    return skinAttr;
  }

  /**
   * 应用皮肤
   */
  public void applySkin() {
    if (SkinCollectionUtil.isEmpty(mSkinInfoList)) {
      return;
    }

    for (SkinInfo info : mSkinInfoList) {
      if (info != null) {
        info.apply();
      }
    }
  }

  /**
   * 清除有皮肤更改需求的View及其对应的属性的集合
   */
  public void clean() {
    if (SkinCollectionUtil.isEmpty(mSkinInfoList)) {
      return;
    }

    for (SkinInfo info : mSkinInfoList) {
      if (info != null) {
        info.clean();
      }
    }
  }

}
