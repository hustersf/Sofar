package com.sofar.skin.attr;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sofar.skin.core.SkinManager;
import com.sofar.skin.model.SkinAttr;

public class TextColorAttr extends SkinAttr {

  @Override
  public void apply(@NonNull View view) {
    if (view instanceof TextView) {
      TextView textView = (TextView) view;
      if (RES_TYPE_NAME_COLOR.equals(attrValueTypeName)) {
        textView.setTextColor(SkinManager.getInstance().getColorStateList(attrValueRefId));
      }
    }
  }
}
