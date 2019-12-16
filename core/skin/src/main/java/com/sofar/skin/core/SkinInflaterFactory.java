package com.sofar.skin.core;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.sofar.skin.attr.AttrFactory;
import com.sofar.skin.config.SkinConfig;
import com.sofar.skin.model.DynamicAttr;
import com.sofar.skin.model.SkinAttr;
import com.sofar.skin.model.SkinInfo;
import com.sofar.skin.util.SkinCollectionUtil;
import com.sofar.skin.util.SkinL;

import java.util.ArrayList;
import java.util.List;

/**
 * 找到需要换肤的View
 * <p>
 * 1.支持xml解析,skin:enable=true
 * 参考{@link LayoutInflater}源码，在解析xml中的view时，会调用Factory2的onCreateView
 * <p>
 * 2.动态添加
 * {@link #dynamicAddSkinEnableView(Context, View, List)}
 */
public class SkinInflaterFactory implements LayoutInflater.Factory2 {

  private AppCompatActivity appCompatActivity;
  private List<SkinInfo> skinInfos;

  public SkinInflaterFactory(@NonNull AppCompatActivity appCompatActivity) {
    this.appCompatActivity = appCompatActivity;
    skinInfos = new ArrayList<>();
  }

  @Override
  public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
    AppCompatDelegate delegate = appCompatActivity.getDelegate();
    View view = delegate.createView(parent, name, context, attrs);

    boolean skinEnable = attrs.getAttributeBooleanValue(SkinConfig.NAMESPACE, SkinConfig.ATTR_SKIN_ENABLE, false);
    if (skinEnable) {
      if (view == null) {
        view = SkinViewInflater.createViewFromTag(context, name, attrs);
      }

      if (view != null) {
        parseSkinAttr(context, attrs, view);
      } else {
        SkinL.d("createView failed:" + name);
      }
    }
    return view;
  }

  /**
   * 此方法其实不会被调用
   */
  @Override
  public View onCreateView(String name, Context context, AttributeSet attrs) {
    return onCreateView(null, name, context, attrs);
  }

  private void parseSkinAttr(Context context, AttributeSet attrs, View view) {
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
          String name = context.getResources().getResourceEntryName(id);
          String type = context.getResources().getResourceTypeName(id);
          SkinAttr skinAttr = AttrFactory.get(attrName, id, name, type);
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

    if (!SkinCollectionUtil.isEmpty(viewAttrs)) {
      SkinInfo skinInfo = new SkinInfo();
      skinInfo.view = view;
      skinInfo.skinAttrs = viewAttrs;
      skinInfos.add(skinInfo);

      if (SkinManager.getInstance().isExternalSkin()) {
        skinInfo.apply();
      }
    }
  }

  public void dynamicAddSkinEnableView(Context context, View view, String attrName, int attrValueResId) {
    if (!AttrFactory.isSupportedAttr(attrName)) {
      return;
    }

    List<SkinAttr> viewAttrs = new ArrayList<>();
    String name = context.getResources().getResourceEntryName(attrValueResId);
    String type = context.getResources().getResourceTypeName(attrValueResId);
    SkinAttr skinAttr = AttrFactory.get(attrName, attrValueResId, name, type);
    if (skinAttr != null) {
      viewAttrs.add(skinAttr);
      SkinInfo skinInfo = new SkinInfo();
      skinInfo.view = view;
      skinInfo.skinAttrs = viewAttrs;
      skinInfos.add(skinInfo);

      skinInfo.apply();
    }
  }

  public void dynamicAddSkinEnableView(Context context, View view, @NonNull List<DynamicAttr> dynamicAttrs) {
    List<SkinAttr> viewAttrs = new ArrayList<>();
    for (DynamicAttr attr : dynamicAttrs) {
      if (attr == null || !AttrFactory.isSupportedAttr(attr.attrName)) {
        continue;
      }

      String name = context.getResources().getResourceEntryName(attr.refResId);
      String type = context.getResources().getResourceTypeName(attr.refResId);

      SkinAttr skinAttr = AttrFactory.get(attr.attrName, attr.refResId, name, type);
      if (skinAttr != null) {
        viewAttrs.add(skinAttr);
      }
    }

    SkinInfo skinInfo = new SkinInfo();
    skinInfo.view = view;
    skinInfo.skinAttrs = viewAttrs;
    skinInfos.add(skinInfo);

    skinInfo.apply();
  }

  /**
   * 应用皮肤
   */
  public void applySkin() {
    if (SkinCollectionUtil.isEmpty(skinInfos)) {
      return;
    }

    for (SkinInfo info : skinInfos) {
      if (info != null) {
        info.apply();
      }
    }
  }

  /**
   * 清除有皮肤更改需求的View及其对应的属性的集合
   */
  public void clean() {
    if (SkinCollectionUtil.isEmpty(skinInfos)) {
      return;
    }

    for (SkinInfo info : skinInfos) {
      if (info != null) {
        info.clean();
      }
    }
  }

}
