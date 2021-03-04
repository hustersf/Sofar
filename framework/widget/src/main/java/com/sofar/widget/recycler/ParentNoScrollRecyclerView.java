package com.sofar.widget.recycler;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 指定一个方向禁止父View响应滑动事件
 */
public class ParentNoScrollRecyclerView extends RecyclerView {

  private static final String TAG = "ParentNoScrollRecyclerView";

  private int touchSlop;
  private PointF downPoint = new PointF();
  private int orientation;

  public ParentNoScrollRecyclerView(@NonNull Context context) {
    this(context, null);
  }

  public ParentNoScrollRecyclerView(@NonNull Context context,
    @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ParentNoScrollRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs,
    int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  private void init(@NonNull Context context) {
    touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
  }

  public void setDisallowOrientation(@Orientation int orientation) {
    this.orientation = orientation;
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    boolean disallow = true;
    int action = ev.getAction();
    switch (action) {
      case MotionEvent.ACTION_DOWN:
        downPoint.set(ev.getX(), ev.getY());
        break;
      case MotionEvent.ACTION_MOVE:
        float x = ev.getX();
        float y = ev.getY();
        float dx = x - downPoint.x;
        float dy = y - downPoint.y;
        if (orientation == RecyclerView.HORIZONTAL) {
          disallow = Math.abs(dx) > Math.abs(dy);
        } else {
          disallow = Math.abs(dy) > Math.abs(dx);
        }
        break;
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        disallow = false;
        break;
    }
    Log.d(TAG, "disallow=" + disallow);
    getParent().requestDisallowInterceptTouchEvent(disallow);
    return super.onInterceptTouchEvent(ev);
  }
}
