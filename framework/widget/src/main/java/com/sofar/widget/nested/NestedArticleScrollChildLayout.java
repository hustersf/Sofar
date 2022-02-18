package com.sofar.widget.nested;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * {@link NestedArticleScrollLayout} 的直接子View
 * <p>
 * 解决嵌套 WebView/RecyclerView 时，高度不受限制，导致的复用失效问题
 * 最大高度限制为 NestedArticleScrollLayout 的高度
 */
public class NestedArticleScrollChildLayout extends ViewGroup {

  public NestedArticleScrollChildLayout(Context context) {
    super(context);
  }

  public NestedArticleScrollChildLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public NestedArticleScrollChildLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);
    heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.AT_MOST);

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

    setMeasuredDimension(getDefaultSize(width, widthMeasureSpec),
      MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
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
  }

  @Override
  public LayoutParams generateLayoutParams(AttributeSet attrs) {
    return new MarginLayoutParams(getContext(), attrs);
  }
}
