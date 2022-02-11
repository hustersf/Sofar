package com.sofar.widget.nested;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.NonNull;

public class NestedLinkWebView extends NestedWebView implements NestedLinkScrollChild {

  private OnNestedScrollListener mNestedScrollListener;

  NestedWebView.OnScrollListener mScrollListener = new OnScrollListener() {
    @Override
    public void onScrollStateChanged(int newState) {
      super.onScrollStateChanged(newState);
      if (mNestedScrollListener != null) {
        mNestedScrollListener.onNestedScrollStateChanged(NestedLinkWebView.this, newState);
      }
    }

    @Override
    public void onScrolled(int dx, int dy) {
      super.onScrolled(dx, dy);
      if (mNestedScrollListener != null) {
        mNestedScrollListener.onNestedScrolled(NestedLinkWebView.this, dx, dy);
      }
    }
  };

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

  @Override
  public void setOnNestedScrollListener(@NonNull OnNestedScrollListener listener) {
    mNestedScrollListener = listener;
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    addOnScrollListener(mScrollListener);
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    removeOnScrollListener(mScrollListener);
  }

}
