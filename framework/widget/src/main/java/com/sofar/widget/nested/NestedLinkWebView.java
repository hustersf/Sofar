package com.sofar.widget.nested;

import android.content.Context;
import android.util.AttributeSet;

public class NestedLinkWebView extends NestedWebView implements NestedLinkScrollChild {

  public NestedLinkWebView(Context context) {
    super(context);
  }

  public NestedLinkWebView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public NestedLinkWebView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public boolean fling(int velocityY) {
    return super.fling(velocityY);
  }

  @Override
  public void scrollToTop() {
    final int oldScrollY = getScrollY();
    scrollBy(0, -oldScrollY);
  }

  @Override
  public void scrollToBottom() {
    final int oldScrollY = getScrollY();
    final int range = getWebViewContentHeight() - getHeight();
    int dy = range - oldScrollY;
    scrollBy(0, dy);
  }
}
