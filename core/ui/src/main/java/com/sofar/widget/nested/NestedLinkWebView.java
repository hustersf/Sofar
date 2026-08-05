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
    scrollTo(0, 0);
  }

  @Override
  public void scrollToBottom() {
    final int range = getWebViewContentHeight() - getHeight();
    scrollTo(0, range);
  }

  @Override
  public void setOnNestedScrollListener(OnNestedScrollListener listener) {
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
