package com.sofar.widget.recycler.layoutmanager.stack;

import android.view.View;

public class DefaultLayout extends StackLayout {

  private boolean mHasMeasureItemSize = false;
  private int mWidthSpace = 0;
  private int mHeightSpace = 0;
  private int mStartMargin = 0;

  private int mWidth = 0;
  private int mHeight = 0;
  private int mScrollOffset = 0;

  public DefaultLayout(int orientation, int visibleCount, int itemOffset) {
    super(orientation, visibleCount, itemOffset);
  }

  @Override
  void doLayout(StackLayoutManager stackLayoutManager, int scrollOffset, float firstMovePercent,
    View itemView, int position) {
    mWidth = stackLayoutManager.getWidth();
    mHeight = stackLayoutManager.getHeight();
    mScrollOffset = scrollOffset;
    if (!mHasMeasureItemSize) {
      mWidthSpace = mWidth - stackLayoutManager.getDecoratedMeasuredWidth(itemView);
      mHeightSpace = mHeight - stackLayoutManager.getDecoratedMeasuredHeight(itemView);
      mStartMargin = getStartMargin();
      mHasMeasureItemSize = true;
    }
    int left;
    int top;
    if (position == 0) {
      left = getFirstVisibleItemLeft();
      top = getFirstVisibleItemTop();
    } else {
      left = getAfterFirstVisibleItemLeft(position, firstMovePercent);
      top = getAfterFirstVisibleItemTop(position, firstMovePercent);
    }

    int right = left + stackLayoutManager.getDecoratedMeasuredWidth(itemView);
    int bottom = top + stackLayoutManager.getDecoratedMeasuredHeight(itemView);

    stackLayoutManager.layoutDecorated(itemView, left, top, right, bottom);
  }

  @Override
  void requestLayout() {
    //表示尺寸可能发生了改变
    mHasMeasureItemSize = false;
  }

  private int getFirstVisibleItemLeft() {
    switch (orientation) {
      case StackLayoutManager.Orientation.RIGHT_TO_LEFT:
        return mStartMargin - mScrollOffset % mWidth;
      case StackLayoutManager.Orientation.LEFT_TO_RIGHT:
        if (mScrollOffset % mWidth == 0) {
          return mStartMargin;
        } else {
          return mStartMargin + (mWidth - mScrollOffset % mWidth);
        }
      default:
        return mWidthSpace / 2;
    }
  }

  private int getFirstVisibleItemTop() {
    switch (orientation) {
      case StackLayoutManager.Orientation.BOTTOM_TO_TOP:
        return mStartMargin - mScrollOffset % mHeight;
      case StackLayoutManager.Orientation.TOP_TO_BOTTOM:
        if (mScrollOffset % mWidth == 0) {
          return mStartMargin;
        } else {
          return mStartMargin + (mHeight - mScrollOffset % mHeight);
        }
      default:
        return mHeightSpace / 2;
    }
  }

  private int getAfterFirstVisibleItemLeft(int visiblePosition, float movePercent) {
    switch (orientation) {
      case StackLayoutManager.Orientation.RIGHT_TO_LEFT:
        return (int) (mStartMargin + itemOffset * (visiblePosition - movePercent));
      case StackLayoutManager.Orientation.LEFT_TO_RIGHT:
        return (int) (mStartMargin - itemOffset * (visiblePosition - movePercent));
      default:
        return mWidthSpace / 2;
    }
  }

  private int getAfterFirstVisibleItemTop(int visiblePosition, float movePercent) {
    switch (orientation) {
      case StackLayoutManager.Orientation.BOTTOM_TO_TOP:
        return (int) (mStartMargin + itemOffset * (visiblePosition - movePercent));
      case StackLayoutManager.Orientation.TOP_TO_BOTTOM:
        return (int) (mStartMargin - itemOffset * (visiblePosition - movePercent));
      default:
        return mHeightSpace / 2;
    }
  }

  protected int getStartMargin() {
    switch (orientation) {
      case StackLayoutManager.Orientation.RIGHT_TO_LEFT:
      case StackLayoutManager.Orientation.LEFT_TO_RIGHT:
        return mWidthSpace / 2;
      default:
        return mHeightSpace / 2;
    }
  }

}
