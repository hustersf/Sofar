package com.sofar.fun.play.core;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AutoPlayHelper {

  private final String TAG = "AutoPlayHelper";

  private static final int INVALID_POSITION = -1;

  private RecyclerView mRecyclerView;
  private int playPosition = INVALID_POSITION;
  private int markPosition = INVALID_POSITION;
  private SelectListener listener;

  RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
      super.onScrollStateChanged(recyclerView, newState);
      if (newState == RecyclerView.SCROLL_STATE_IDLE) {
        findOnePlay();
      } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
        markPosition = INVALID_POSITION;
      }
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
      super.onScrolled(recyclerView, dx, dy);
    }
  };


  public void attachToRecyclerView(@NonNull RecyclerView recyclerView) {
    if (mRecyclerView != null) {
      mRecyclerView.removeOnScrollListener(scrollListener);
    }

    mRecyclerView = recyclerView;
    mRecyclerView.addOnScrollListener(scrollListener);
    recyclerView.post(() -> {
      findOnePlay();
    });
  }

  /**
   * 找到列表中的第一个在屏幕中占比例最大的view，认定它是焦点
   */
  private void findOnePlay() {
    float maxRatio = 0;
    View maxChildView = null;
    for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
      View childView = mRecyclerView.getChildAt(i);
      if (childView != null) {
        float ratio = getViewShowRatio(childView);
        int position = mRecyclerView.getChildAdapterPosition(childView);
        if (ratio > maxRatio || markPosition == position) {
          maxRatio = ratio;
          maxChildView = childView;
        }
      }
    }

    if (maxChildView != null) {
      play(maxChildView);
    }
  }

  private void play(@NonNull View view) {
    playPosition = mRecyclerView.getChildAdapterPosition(view);
    Log.d(TAG, "play position=" + playPosition);
    if (listener != null) {
      listener.onSelected(view, playPosition);
    }
  }

  /**
   * 自动滚动到下一个位置，并播放
   */
  public void playFinish() {
    markPosition = playPosition + 1;
    View nextView = mRecyclerView.getLayoutManager().findViewByPosition(markPosition);
    if (nextView != null) {
      int top = nextView.getTop();
      mRecyclerView.smoothScrollBy(0, top);
    } else {
      Log.d(TAG, "no play item");
    }
  }

  private float getViewShowRatio(@NonNull View view) {
    if (!view.isShown()) {
      return 0;
    }

    Rect rect = new Rect();
    if (view.getGlobalVisibleRect(rect)) {
      long visibleArea = (long) rect.height() * (long) rect.width();
      long viewArea = (long) view.getHeight() * (long) view.getWidth();
      float ratio = 1.0f * visibleArea / viewArea;
      Log.d(TAG, "ratio=" + ratio);
      return ratio;
    }
    return 0;
  }

  public void setOnSelectListener(SelectListener listener) {
    this.listener = listener;
  }

  public interface SelectListener {
    void onSelected(View childView, int position);
  }

}
