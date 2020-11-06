package com.sofar.skin.attr;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sofar.skin.core.SkinColorWhiteList;
import com.sofar.skin.core.SkinManager;
import com.sofar.skin.model.SkinAttr;

public class TextColorAttr extends SkinAttr {

  @Override
  public void apply(@NonNull View view) {
    if (view instanceof TextView) {
      TextView textView = (TextView) view;
      if (RES_TYPE_NAME_COLOR.equals(attrValueTypeName)) {
        if (SkinManager.getInstance().isColorSkin() && SkinColorWhiteList.isSupportResName(attrValueRefName)) {
          textView.setTextColor(SkinManager.getInstance().getColor(attrValueRefId));
        } else {
          textView.setTextColor(SkinManager.getInstance().getColorStateList(attrValueRefId));
        }
      }
    }
  }
}
