package com.sofar.widget.recycler;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LinearLayoutManagerAccurateOffset extends LinearLayoutManager {

  private SparseIntArray childHeightMap = new SparseIntArray();

  public LinearLayoutManagerAccurateOffset(Context context) {
    super(context);
  }

  public LinearLayoutManagerAccurateOffset(Context context, int orientation,
    boolean reverseLayout) {
    super(context, orientation, reverseLayout);
  }

  public LinearLayoutManagerAccurateOffset(Context context, AttributeSet attrs,
    int defStyleAttr,
    int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Override
  public void onLayoutCompleted(RecyclerView.State state) {
    super.onLayoutCompleted(state);
    for (int i = 0; i < getChildCount(); i++) {
      View child = getChildAt(i);
      if (child == null) {
        continue;
      }
      childHeightMap.put(getPosition(child), child.getHeight());
    }
  }

  @Override
  public int computeVerticalScrollOffset(RecyclerView.State state) {
    View firstChild = getChildAt(0);
    if (firstChild == null) {
      return 0;
    }

    int firstChildPosition = getPosition(firstChild);
    int scrollY = -(int) firstChild.getY();
    for (int i = 0; i < firstChildPosition; i++) {
      scrollY += childHeightMap.get(i);
    }
    return scrollY;
  }
}
