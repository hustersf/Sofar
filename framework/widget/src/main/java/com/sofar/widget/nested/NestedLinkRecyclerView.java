package com.sofar.widget.nested;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class NestedLinkRecyclerView extends RecyclerView implements NestedLinkScrollChild {

  private int maxHeight;

  private OnNestedScrollListener mNestedScrollListener;

  private RecyclerView.OnScrollListener mScrollListener = new OnScrollListener() {
    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
      super.onScrollStateChanged(recyclerView, newState);
      if (mNestedScrollListener != null) {
        mNestedScrollListener.onNestedScrollStateChanged(recyclerView, newState);
      }
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
      super.onScrolled(recyclerView, dx, dy);
      if (mNestedScrollListener != null) {
        mNestedScrollListener.onNestedScrolled(recyclerView, dx, dy);
      }
    }
  };

  public NestedLinkRecyclerView(@NonNull Context context) {
    super(context);
  }

  public NestedLinkRecyclerView(@NonNull Context context,
    @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public NestedLinkRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs,
    int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public boolean fling(int velocityY) {
    return fling(0, velocityY);
  }

  @Override
  public void scrollToTop() {
    scrollToPosition(0);
  }

  @Override
  public void scrollToBottom() {
    scrollToPosition(getAdapter().getItemCount() - 1);
  }

  @Override
  public void setOnNestedScrollListener(@NonNull OnNestedScrollListener listener) {
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

  public void setMaxHeight(int maxHeight) {
    this.maxHeight = maxHeight;
  }

  @Override
  protected void onMeasure(int widthSpec, int heightSpec) {
    if (maxHeight > 0) {
      heightSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
    }
    super.onMeasure(widthSpec, heightSpec);
  }

}
