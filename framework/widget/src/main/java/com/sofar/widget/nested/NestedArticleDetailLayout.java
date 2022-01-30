package com.sofar.widget.nested;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;

/**
 * 处理文章详情页嵌套滑动的容器
 * NestedWebView + RecyclerView
 */
public class NestedArticleDetailLayout extends ViewGroup implements NestedScrollingParent3 {
  private static String TAG = "NestedArticleDetailLayout";

  final int[] mReusableIntPair = new int[2];

  private final NestedScrollingParentHelper mParentHelper;
  private int mScrollThreshold;
  private int mChildTotalHeight;

  public NestedArticleDetailLayout(Context context) {
    this(context, null);
  }

  public NestedArticleDetailLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public NestedArticleDetailLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mParentHelper = new NestedScrollingParentHelper(this);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int width = 0;
    int height = 0;
    for (int i = 0; i < getChildCount(); i++) {
      View child = getChildAt(i);
      if (child == null || child.getVisibility() == GONE) {
        continue;
      }

      measureChild(child, widthMeasureSpec, heightMeasureSpec);
      MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
      int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
      int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

      width = Math.max(width, childWidth);
      height += childHeight;
    }

    mChildTotalHeight = height;
    setMeasuredDimension(getDefaultSize(width, widthMeasureSpec),
      getDefaultSize(height, heightMeasureSpec));
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    int left = getPaddingLeft();
    int top = getPaddingTop();
    for (int i = 0; i < getChildCount(); i++) {
      View child = getChildAt(i);
      if (child == null || child.getVisibility() == GONE) {
        continue;
      }

      MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
      int cl = left + lp.leftMargin;
      int ct = top + lp.topMargin;
      int cr = cl + child.getMeasuredWidth();
      int cb = ct + child.getMeasuredHeight();
      child.layout(cl, ct, cr, cb);
      top += child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
    }

    mScrollThreshold = mChildTotalHeight - getMeasuredHeight();
    if (mScrollThreshold < 0) {
      mScrollThreshold = 0;
    }
  }

  // NestedScrollingParent3

  @Override
  public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed,
    int dyUnconsumed, int type, @NonNull int[] consumed) {
    onNestedScrollInternal(target, dyUnconsumed, type, consumed);
  }

  private void onNestedScrollInternal(@NonNull View target, int dyUnconsumed, int type,
    @Nullable int[] consumed) {
    if (dyUnconsumed != 0) {
      mReusableIntPair[0] = 0;
      mReusableIntPair[1] = 0;
      scrollStep(dyUnconsumed, mReusableIntPair);
      final int myConsumed = mReusableIntPair[1];
      if (consumed != null) {
        consumed[1] += myConsumed;
      }
      final int myUnconsumed = dyUnconsumed - myConsumed;
      Log.d(TAG, "onNestedScroll parent start scroll myUnconsumed=" + myUnconsumed);
     // findPreOrNextChildScroll(target, myUnconsumed);
    }
  }

  /**
   * 尝试解决子View之间 fling 操作戛然而止的问题
   *
   * @param target
   * @param dyUnconsumed >0 上滑(手指从下到上)
   */
  private void findPreOrNextChildScroll(@NonNull View target, int dyUnconsumed) {
    View childView = null;
    if (dyUnconsumed > 0) {
      for (int i = 0; i < getChildCount(); i++) {
        View child = getChildAt(i);
        if (child == target) {
          childView = getChildAt(i + 1);
          break;
        }
      }
    }
    if (dyUnconsumed < 0) {
      for (int i = 0; i < getChildCount(); i++) {
        View child = getChildAt(i);
        if (child == target) {
          childView = getChildAt(i - 1);
          break;
        }
      }
    }
    if (childView == null || dyUnconsumed == 0) {
      return;
    }

    childView.scrollBy(0, dyUnconsumed);
  }

  // NestedScrollingParent2

  @Override
  public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes,
    int type) {
    return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
  }

  @Override
  public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes,
    int type) {
    mParentHelper.onNestedScrollAccepted(child, target, axes, type);
  }

  @Override
  public void onStopNestedScroll(@NonNull View target, int type) {
    mParentHelper.onStopNestedScroll(target, type);
  }

  @Override
  public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed,
    int dyUnconsumed, int type) {
    onNestedScrollInternal(target, dyUnconsumed, type, null);
  }

  /**
   * @param target   产生嵌套滑动事件的子View
   * @param dx
   * @param dy       >0 上滑(手指从下到上)
   * @param consumed 记录已经消费的距离，子View在消费事件会先减掉这部分距离
   * @param type
   */
  @Override
  public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed,
    int type) {
    boolean scrollTop = !target.canScrollVertically(-1);
    boolean scrollBottom = !target.canScrollVertically(1);
    if (getScrollY() > 0 && getScrollY() < mScrollThreshold) {
      Log.d(TAG, "onNestedPreScroll parent start scroll");
      mReusableIntPair[0] = 0;
      mReusableIntPair[1] = 0;
      scrollStep(dy, mReusableIntPair);
      final int myConsumed = mReusableIntPair[1];
      consumed[1] += myConsumed;
    }
  }

  // NestedScrollingParent

  @Override
  public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
    return super.onNestedFling(target, velocityX, velocityY, consumed);
  }

  @Override
  public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
    return super.onNestedPreFling(target, velocityX, velocityY);
  }

  @Override
  public int getNestedScrollAxes() {
    return mParentHelper.getNestedScrollAxes();
  }

  void scrollStep(int dy, @Nullable int[] consumed) {
    final int oldScrollY = getScrollY();
    int preScroll = dy + oldScrollY;
    if (preScroll < 0) {
      preScroll = 0;
    }
    if (preScroll > mScrollThreshold) {
      preScroll = mScrollThreshold;
    }

    dy = preScroll - oldScrollY;
    scrollBy(0, dy);
    final int myConsumed = getScrollY() - oldScrollY;
    if (consumed != null) {
      consumed[1] = myConsumed;
    }
  }

  @Override
  public LayoutParams generateLayoutParams(AttributeSet attrs) {
    return new MarginLayoutParams(getContext(), attrs);
  }

}
