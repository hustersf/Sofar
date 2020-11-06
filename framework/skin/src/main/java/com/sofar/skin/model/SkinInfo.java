package com.sofar.skin.model;

import android.view.View;

import com.sofar.skin.util.SkinCollectionUtil;

import java.util.List;

/**
 * 存储那些有皮肤更换需求的View及其对应的属性
 */
public class SkinInfo {

  public View view;

  public List<SkinAttr> skinAttrs;

  public void apply() {
    if (view == null || SkinCollectionUtil.isEmpty(skinAttrs)) {
      return;
    }

    for (SkinAttr attr : skinAttrs) {
      if (attr != null) {
        attr.apply(view);
      }
    }
  }

  public void clean() {
    if (view == null || SkinCollectionUtil.isEmpty(skinAttrs)) {
      return;
    }

    view = null;
    for (SkinAttr attr : skinAttrs) {
      attr = null;
    }
  }
}
