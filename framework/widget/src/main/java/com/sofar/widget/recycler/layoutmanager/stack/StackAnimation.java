package com.sofar.widget.recycler.layoutmanager.stack;

import android.view.View;

public abstract class StackAnimation {

  protected int orientation;
  protected int visibleCount;

  public StackAnimation(@StackLayoutManager.Orientation int orientation, int visibleCount) {
    this.orientation = orientation;
    this.visibleCount = visibleCount;
  }

  void setVisibleCount(int visibleCount) {
    this.visibleCount = visibleCount;
  }

  /**
   * 外部回调，用来做动画.
   *
   * @param firstMovePercent 第一个可视 item 移动的百分比，当即将完全移出屏幕的时候 firstMovePercent无限接近1.
   * @param itemView         当前的 itemView.
   * @param position         当前 itemView 对应的位置，position = 0 until visibleCount.
   */
  abstract void doAnimation(float firstMovePercent, View itemView, int position);
}
