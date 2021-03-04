package com.sofar.widget.recycler.layoutmanager.stack;

import android.view.View;

public class DefaultAnimation extends StackAnimation {

  private float mScale = 0.95f;
  private float mOutScale = 0.8f;
  private int mOutRotation;

  public DefaultAnimation(int orientation, int visibleCount) {
    super(orientation, visibleCount);
    init();
  }

  private void init() {
    if (orientation == StackLayoutManager.Orientation.RIGHT_TO_LEFT ||
      orientation == StackLayoutManager.Orientation.LEFT_TO_RIGHT) {
      mOutRotation = 10;
    } else {
      mOutRotation = 0;
    }
  }

  /**
   * 设置 item 缩放比例.
   *
   * @param scale 缩放比例，默认是0.95f.
   */
  public void setItemScaleRate(float scale) {
    mScale = scale;
  }

  /**
   * 获取item缩放比例.
   *
   * @return item缩放比例，默认是0.95f.
   */
  public float getItemScaleRate() {
    return mScale;
  }

  /**
   * 设置 itemView 离开屏幕时候的缩放比例.
   *
   * @param scale 缩放比例，默认是0.8f.
   */
  public void setOutScale(float scale) {
    mOutScale = scale;
  }

  /**
   * 获取 itemView 离开屏幕时候的缩放比例.
   *
   * @return 缩放比例，默认是0.8f.
   */
  public float getOutScale() {
    return mOutScale;
  }

  /**
   * 设置 itemView 离开屏幕时候的旋转角度.
   *
   * @param rotation 旋转角度，默认是30.
   */
  public void setOutRotation(int rotation) {
    mOutRotation = rotation;
  }

  /**
   * 获取 itemView 离开屏幕时候的旋转角度
   *
   * @return 旋转角度，默认是30
   */
  public int getOutRotation() {
    return mOutRotation;
  }

  @Override
  void doAnimation(float firstMovePercent, View itemView, int position) {
    float scale;
    float alpha = 1.0f;
    float rotation;
    if (position == 0) {
      scale = 1 - ((1 - mOutScale) * firstMovePercent);
      rotation = mOutRotation * firstMovePercent;
    } else {
      float minScale = (float) Math.pow(mScale, position);
      float maxScale = (float) Math.pow(mScale, (position - 1));
      scale = minScale + (maxScale - minScale) * firstMovePercent;
      //只对最后一个 item 做透明度变化
      if (position == visibleCount) {
        alpha = firstMovePercent;
      }
      rotation = 0f;
    }

    setItemPivotXY(orientation, itemView);
    rotationFirstVisibleItem(orientation, itemView, rotation);
    itemView.setScaleX(scale);
    itemView.setScaleY(scale);
    itemView.setAlpha(alpha);
  }

  private void setItemPivotXY(int orientation, View view) {
    switch (orientation) {
      case StackLayoutManager.Orientation.RIGHT_TO_LEFT:
        view.setPivotX(view.getMeasuredWidth());
        view.setPivotY(view.getMeasuredHeight() / 2);
        break;
      case StackLayoutManager.Orientation.LEFT_TO_RIGHT:
        view.setPivotX(0);
        view.setPivotY(view.getMeasuredHeight() / 2);
        break;
      case StackLayoutManager.Orientation.BOTTOM_TO_TOP:
        view.setPivotX(view.getMeasuredWidth() / 2);
        view.setPivotY(view.getMeasuredHeight());
        break;
      case StackLayoutManager.Orientation.TOP_TO_BOTTOM:
        view.setPivotX(view.getMeasuredWidth() / 2);
        view.setPivotY(0);
        break;
    }
  }

  private void rotationFirstVisibleItem(int orientation, View view, float rotation) {
    switch (orientation) {
      case StackLayoutManager.Orientation.RIGHT_TO_LEFT:
        view.setRotationY(rotation);
        break;
      case StackLayoutManager.Orientation.LEFT_TO_RIGHT:
        view.setRotationY(-rotation);
        break;
      case StackLayoutManager.Orientation.BOTTOM_TO_TOP:
        view.setRotationX(-rotation);
        break;
      case StackLayoutManager.Orientation.TOP_TO_BOTTOM:
        view.setRotationX(rotation);
        break;
    }
  }
}
