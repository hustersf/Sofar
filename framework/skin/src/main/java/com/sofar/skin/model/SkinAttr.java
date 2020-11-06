package com.sofar.skin.model;

import android.view.View;

import androidx.annotation.NonNull;

public abstract class SkinAttr implements Cloneable {

  protected static final String RES_TYPE_NAME_COLOR = "color";
  protected static final String RES_TYPE_NAME_DRAWABLE = "drawable";
  /**
   * 属性名, 例如: background、textSize、textColor
   */
  public String attrName;

  /**
   * 属性值的引用id
   */
  public int attrValueRefId;

  /**
   * 资源的名字, 例如 [app_exit_btn_background]
   */
  public String attrValueRefName;

  /**
   * type of the value , such as color or drawable
   */
  public String attrValueTypeName;

  public abstract void apply(@NonNull View view);

  @NonNull
  @Override
  public String toString() {
    return "SkinAttr={"
      + "attrName=" + attrName + ","
      + "attrValueRefId=" + attrValueRefId + ","
      + "attrValueRefName=" + attrValueRefName + ","
      + "attrValueTypeName=" + attrValueTypeName + "}";
  }

  @Override
  public SkinAttr clone() {
    SkinAttr o = null;
    try {
      o = (SkinAttr) super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return o;
  }


}
