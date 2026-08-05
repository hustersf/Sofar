package com.sofar.widget.recycler.layoutmanager.stack;

import android.view.View;

public abstract class StackLayout {

  protected int orientation;
  protected int visibleCount;
  protected int itemOffset;

  public StackLayout(@StackLayoutManager.Orientation int orientation, int visibleCount,
    int itemOffset) {
    this.orientation = orientation;
    this.visibleCount = visibleCount;
    this.itemOffset = itemOffset;
  }

  void setItemOffset(int offset) {
    itemOffset = offset;
  }

  int getItemOffset() {
    return itemOffset;
  }

  /**
   * 外部回调，用来做布局.
   *
   * @param firstMovePercent 第一个可视 item 移动的百分比，当即将完全移出屏幕的时候 firstMovePercent无限接近1.
   * @param itemView         当前的 itemView.
   * @param position         当前 itemView 对应的位置，position = 0 until visibleCount.
   */
  abstract void doLayout(StackLayoutManager stackLayoutManager, int scrollOffset,
    float firstMovePercent, View itemView, int position);

  abstract void requestLayout();
}
