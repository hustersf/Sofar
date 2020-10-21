package com.sofar.widget.recycler;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * 处理 {@link StaggeredGridLayoutManager} 的分割，控制分割的大小.
 */
public class StaggeredGridMarginItemDecoration extends RecyclerView.ItemDecoration {

  private final int mTopBottomSpace;
  private final int mLeftRightSpace;
  private final int mItemSpace;
  private final int mSpanCount;

  public StaggeredGridMarginItemDecoration(int spanCount, int itemSpace) {
    this(spanCount, itemSpace, itemSpace, itemSpace);
  }

  public StaggeredGridMarginItemDecoration(int spanCount, int itemSpace, int topBottomSpace, int leftRightSpace) {
    this.mSpanCount = spanCount;
    this.mItemSpace = itemSpace;
    this.mTopBottomSpace = topBottomSpace;
    this.mLeftRightSpace = leftRightSpace;
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    final int totalCount = parent.getAdapter().getItemCount();
    final int childPosition = parent.getChildAdapterPosition(view);
    StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
    int spanIndex = layoutParams.getSpanIndex();
    outRect.left = mItemSpace / 2;
    outRect.right = mItemSpace / 2;
    outRect.top = mItemSpace;
    outRect.bottom = 0;
    if (spanIndex == 0) {
      outRect.left = mLeftRightSpace;
    } else if (spanIndex == mSpanCount - 1) {
      outRect.right = mLeftRightSpace;
    }
    boolean firstLine = childPosition < mSpanCount;
    int maxLineIndex = (totalCount - 1) / mSpanCount;
    int currentLineIndex = childPosition / mSpanCount;
    boolean lastLine = maxLineIndex == currentLineIndex;
    if (firstLine) {
      outRect.top = mTopBottomSpace;
    } else if (lastLine) {
      outRect.bottom = mTopBottomSpace;
    }
  }
}
