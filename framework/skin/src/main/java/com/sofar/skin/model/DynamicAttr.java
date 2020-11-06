package com.sofar.skin.model;

public class DynamicAttr {

  /**
   * 属性名{@link com.sofar.skin.attr.AttrFactory#supportAttrs}
   */
  public String attrName;

  /**
   * 资源引用id
   */
  public int refResId;

  public DynamicAttr(String attrName, int refResId) {
    this.attrName = attrName;
    this.refResId = refResId;
  }
}
