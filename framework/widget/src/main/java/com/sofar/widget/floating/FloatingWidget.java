package com.sofar.widget.floating;

import android.app.Activity;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;

import com.sofar.widget.Util;

/**
 * 悬浮控件，支持拖动
 */
public class FloatingWidget extends FrameLayout {

  private static final String TAG = "FloatingWidget";

  @NonNull
  Activity activity;
  DragHelper dragHelper;

  int screenWidth;
  int screenHeight;
  int statusBarHeight;
  Rect insets = new Rect();

  float xRatio;
  float yRatio;
  DragCallback dragCallback = new DragCallback();

  int width;
  int height;

  public FloatingWidget(@NonNull Activity activity) {
    super(activity);
    this.activity = activity;
    dragHelper = DragHelper.create(this, dragCallback);
    screenWidth = Util.getMetricsWidth(activity);
    screenHeight = Util.getMetricsHeight(activity);
    statusBarHeight = Util.getStatusBarHeight(activity);
  }

  public void attach() {
    if (isAttached()) {
      return;
    }

    ViewGroup parent = activity.findViewById(android.R.id.content);
    attach(parent);
  }

  public void attach(@NonNull ViewGroup parent) {
    if (isAttached()) {
      return;
    }

    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
      width > 0 || width == ViewGroup.LayoutParams.MATCH_PARENT ? width
        : ViewGroup.LayoutParams.WRAP_CONTENT,
      height > 0 || height == ViewGroup.LayoutParams.MATCH_PARENT ? height
        : ViewGroup.LayoutParams.WRAP_CONTENT);
    parent.addView(this, lp);
  }

  public boolean isAttached() {
    return getParent() != null;
  }

  public void setInsets(int left, int top, int right, int bottom) {
    insets.set(left, top, right, bottom);
  }

  public void setSize(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public void setScreenRatio(float x, float y) {
    this.xRatio = x;
    this.yRatio = y;
  }

  private void applyScreenRatio() {
    if (getWidth() == 0 || getHeight() == 0) {
      return;
    }

    float x = xRatio * screenWidth;
    float y = yRatio * screenHeight;
    moveTo(x, y);
  }

  private void moveTo(float x, float y) {
    setX(dragCallback.clampX((int) x));
    setY(dragCallback.clampY((int) y));
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    applyScreenRatio();
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    return super.dispatchTouchEvent(ev);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    getParent().requestDisallowInterceptTouchEvent(true);
    return dragHelper.shouldInterceptTouchEvent(ev);
  }


  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return dragHelper.processTouchEvent(event);
  }

  class DragCallback implements DragHelper.Callback {

    @Override
    public void onDragStart() {
      Log.d(TAG, "onDragStart");
    }

    @Override
    public void onDragEnd() {
      Log.d(TAG, "onDragEnd");
    }

    @Override
    public void onDragOffset(int dx, int dy) {
      Log.d(TAG, "onDragOffset");
    }

    @Override
    public int clampX(int x) {
      return Math.max(Math.min(screenWidth - insets.right - getWidth(), x), insets.left);
    }

    @Override
    public int clampY(int y) {
      return Math
        .max(Math.min(screenHeight - statusBarHeight - insets.bottom - getHeight(), y), insets.top);
    }
  }

}
