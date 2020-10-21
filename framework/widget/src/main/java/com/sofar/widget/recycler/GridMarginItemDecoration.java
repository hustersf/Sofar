package com.sofar.widget.recycler;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 处理 {@link GridLayoutManager} 的分割，控制分割的大小.
 */
public class GridMarginItemDecoration extends RecyclerView.ItemDecoration {

  private final int mSpanCount;
  private final int mItemSpace;
  private final int mLeftSpace;
  private final int mRightSpace;

  public GridMarginItemDecoration(int mSpanCount, int mItemSpace) {
    this(mSpanCount, mItemSpace, 0, 0);
  }

  public GridMarginItemDecoration(int mSpanCount, int mItemSpace, int mLeftSpace, int mRightSpace) {
    this.mSpanCount = mSpanCount;
    this.mItemSpace = mItemSpace;
    this.mLeftSpace = mLeftSpace;
    this.mRightSpace = mRightSpace;
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    int position = parent.getChildAdapterPosition(view);
    int column = position % mSpanCount;
    outRect.top = 0;
    outRect.bottom = mItemSpace;
    if (column == 0) {
      outRect.left = mLeftSpace;
      outRect.right = mItemSpace / 2;
    } else if (column == mSpanCount - 1) {
      outRect.left = mItemSpace / 2;
      outRect.right = mRightSpace;
    } else {
      outRect.left = mItemSpace / 2;
      outRect.right = mItemSpace / 2;
    }
  }
}