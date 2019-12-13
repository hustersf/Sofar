package com.sofar.skin.callback;

import android.view.View;

import com.sofar.skin.model.DynamicAttr;

import java.util.List;

public interface IDynamicNewView {

  void dynamicAddView(View view, List<DynamicAttr> dynamicAttrs);

  void dynamicAddView(View view, String attrName, int attrValueResId);
}
