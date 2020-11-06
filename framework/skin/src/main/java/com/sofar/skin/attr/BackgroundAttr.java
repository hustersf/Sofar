package com.sofar.skin.attr;

import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;

import com.sofar.skin.core.SkinManager;
import com.sofar.skin.model.SkinAttr;

public class BackgroundAttr extends SkinAttr {

  @Override
  public void apply(@NonNull View view) {
    if (RES_TYPE_NAME_COLOR.equals(attrValueTypeName)) {
      int color = SkinManager.getInstance().getColor(attrValueRefId);
      view.setBackgroundColor(color);
    } else if (RES_TYPE_NAME_DRAWABLE.equals(attrValueTypeName)) {
      Drawable drawable = SkinManager.getInstance().getDrawable(attrValueRefId);
      view.setBackground(drawable);
    }
  }
}
