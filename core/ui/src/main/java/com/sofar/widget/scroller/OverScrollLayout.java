package com.sofar.widget.scroller;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.customview.widget.ViewDragHelper;

public class OverScrollLayout extends FrameLayout {

  private static final String TAG = "OverScrollLayout";

  @NonNull
  private ViewDragHelper dragHelper;
  private boolean enable = true;
  private float vThreshold = 0.5f; //竖直方向最多滑动比例
  private float hThreshold = 0.5f; //水平方向最多滑动比例
  private int direction = Direction.UP;  //可滚动的方向
  private OnScrollListener listener;

  public OverScrollLayout(@NonNull Context context) {
    this(context, null);
  }

  public OverScrollLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public OverScrollLayout(@NonNull Context context, @Nullable AttributeSet attrs,
    int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    dragHelper = ViewDragHelper.create(this, new ViewDragCallback());
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    if (enable) {
      return dragHelper.shouldInterceptTouchEvent(ev);
    }
    return super.onInterceptTouchEvent(ev);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (enable) {
      dragHelper.processTouchEvent(event);
      return true;
    }
    return super.onTouchEvent(event);
  }

  @Override
  public void computeScroll() {
    if (dragHelper != null && dragHelper.continueSettling(true)) {
      invalidate();
    }
  }

  private class ViewDragCallback extends ViewDragHelper.Callback {

    @Override
    public boolean tryCaptureView(@NonNull View child, int pointerId) {
      return true;
    }

    @Override
    public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
      super.onViewReleased(releasedChild, xvel, yvel);
      int finalLeft = 0;
      int finalTop = 0;
      dragHelper.settleCapturedViewAt(finalLeft, finalTop);
      invalidate();
    }

    @Override
    public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx,
      int dy) {
      float progress = 0;
      if (direction == Direction.LEFT || direction == Direction.RIGHT) {
        progress = Math.abs(1.0f * left / (getWidth() * hThreshold));
      } else if (direction == Direction.UP || direction == Direction.DOWN) {
        progress = Math.abs(1.0f * top / (getHeight() * vThreshold));
      }
      if (listener != null) {
        listener.onProgress(progress);
      }
      Log.d(TAG, "progress=" + progress);
    }

    @Override
    public int getViewHorizontalDragRange(@NonNull View child) {
      return 1;
    }

    @Override
    public int getViewVerticalDragRange(@NonNull View child) {
      return 1;
    }

    /**
     * 控制child横向移动的边界
     * left表示即将移动到的位置。
     */
    @Override
    public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
      int x = 0;
      if (direction == Direction.LEFT) {
        if (left < 0) {
          x = 0;
        } else if (left > getWidth() * hThreshold) {
          x = (int) (getWidth() * hThreshold);
        } else {
          x = left;
        }
      } else if (direction == Direction.RIGHT) {
        if (left > 0) {
          x = 0;
        } else if (left < -getWidth() * hThreshold) {
          x = (int) (-getWidth() * hThreshold);
        } else {
          x = left;
        }
      }
      return x;
    }

    /**
     * 控制child纵向移动的边界
     */
    @Override
    public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
      int y = 0;
      if (direction == Direction.UP) {
        if (top < 0) {
          y = 0;
        } else if (top > getHeight() * vThreshold) {
          y = (int) (getHeight() * vThreshold);
        } else {
          y = top;
        }
      } else if (direction == Direction.DOWN) {
        if (top > 0) {
          y = 0;
        } else if (top < -getHeight() * vThreshold) {
          y = (int) (-getHeight() * vThreshold);
        } else {
          y = top;
        }
      }
      return y;
    }
  }

  /**
   * 是否允许触发滚动
   */
  public void setEnable(boolean enable) {
    this.enable = enable;
  }

  /**
   * 竖直方向最大滑动比例
   */
  public void setVerticalThreshold(float v) {
    vThreshold = v;
  }

  /**
   * 水平方向最大滑动比例
   */
  public void setHorizontalThreshold(float h) {
    hThreshold = h;
  }

  public void setDirection(@Direction int direction) {
    this.direction = direction;
  }

  @Direction
  public int getDirection() {
    return direction;
  }

  public @interface Direction {
    int LEFT = 0;
    int RIGHT = 1;
    int UP = 2;
    int DOWN = 3;
  }

  public void setOnScrollListener(OnScrollListener listener) {
    this.listener = listener;
  }

  public interface OnScrollListener {
    void onProgress(float progress);
  }
}
