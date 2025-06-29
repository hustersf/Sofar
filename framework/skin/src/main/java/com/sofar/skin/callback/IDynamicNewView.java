package com.sofar.skin.callback;

import android.view.View;

import com.sofar.skin.model.DynamicAttr;

import java.util.List;

import androidx.annotation.NonNull;

public interface IDynamicNewView {

  void dynamicAddView(@NonNull View view, List<DynamicAttr> dynamicAttrs);

  void dynamicAddView(@NonNull View view, String attrName, int attrValueResId);
}
