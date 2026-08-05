package com.sofar.widget.slide;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;

/**
 * 支持上下左右四个方向
 * todo 有待完成
 */
public class SlidingLayout extends ViewGroup {

  private static final String TAG = "SlidingLayout";

  private boolean mInLayout;
  private boolean mFirstLayout = true;
  @NonNull
  private SlidingDragHelper mDragHelper;

  private boolean mLocked;
  @LockDirection
  private int mLockedDirection;

  private View mContentView;
  private View mLeftView;
  private View mRightView;
  private View mTopView;
  private View mBottomView;
  private View mCapturedView;

  static final int[] LAYOUT_ATTRS = new int[]{
    android.R.attr.layout_gravity
  };

  private boolean mEnable = true; // 是否支持滑动
  private float mThreshold = 0.5f; // 触发滑动完成的阈值
  private Point mStartLocation = new Point();

  public SlidingLayout(Context context) {
    this(context, null);
  }

  public SlidingLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SlidingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    mDragHelper = SlidingDragHelper.create(this, new ViewDragCallback());
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    if (mEnable) {
      return mDragHelper.shouldInterceptTouchEvent(ev);
    }
    return super.onInterceptTouchEvent(ev);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (mEnable) {
      mDragHelper.processTouchEvent(event);
      return true;
    }
    return super.onTouchEvent(event);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);
    setMeasuredDimension(widthSize, heightSize);

    final int childCount = getChildCount();
    for (int i = 0; i < childCount; i++) {
      final View child = getChildAt(i);

      if (child.getVisibility() == GONE) {
        continue;
      }

      LayoutParams lp = (LayoutParams) child.getLayoutParams();
      if (isContentView(child)) {
        final int contentWidthSpec = MeasureSpec.makeMeasureSpec(
          widthSize - lp.leftMargin - lp.rightMargin, MeasureSpec.EXACTLY);
        final int contentHeightSpec = MeasureSpec.makeMeasureSpec(
          heightSize - lp.topMargin - lp.bottomMargin, MeasureSpec.EXACTLY);
        child.measure(contentWidthSpec, contentHeightSpec);
      } else if (isSlidingView(child)) {
        final int slideWidthSpec =
          getChildMeasureSpec(widthMeasureSpec, lp.leftMargin + lp.rightMargin, lp.width);
        final int slideHeightSpec =
          getChildMeasureSpec(heightMeasureSpec, lp.topMargin + lp.bottomMargin, lp.height);
        child.measure(slideWidthSpec, slideHeightSpec);
      }
    }
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    mInLayout = true;
    final int width = r - l;
    final int height = b - t;
    final int childCount = getChildCount();
    for (int i = 0; i < childCount; i++) {
      final View child = getChildAt(i);

      if (child.getVisibility() == GONE) {
        continue;
      }

      LayoutParams lp = (LayoutParams) child.getLayoutParams();
      if (isContentView(child)) {
        mContentView = child;
        child.layout(lp.leftMargin, lp.topMargin, lp.leftMargin + child.getMeasuredWidth(),
          lp.topMargin + child.getMeasuredHeight());
      } else {
        //slide view
        boolean isVertical = checkSlideViewAbsoluteGravity(child, Gravity.TOP) ||
          checkSlideViewAbsoluteGravity(child, Gravity.BOTTOM);
        if (isVertical) {
          slideTopAndBottomLayout(child, width, height);
        } else {
          slideLeftAndRightLayout(child, width, height);
        }
      }
    }

    mInLayout = false;
    mFirstLayout = false;
  }

  private void slideLeftAndRightLayout(View child, int width, int height) {
    LayoutParams lp = (LayoutParams) child.getLayoutParams();
    final int childWidth = child.getMeasuredWidth();
    final int childHeight = child.getMeasuredHeight();
    int childLeft;

    final int vgrav = lp.gravity & Gravity.VERTICAL_GRAVITY_MASK;

    if (checkSlideViewAbsoluteGravity(child, Gravity.LEFT)) {
      childLeft = -childWidth;
      mLeftView = child;
    } else {
      childLeft = width;
      mRightView = child;
    }

    switch (vgrav) {
      default:
      case Gravity.TOP: {
        child.layout(childLeft, lp.topMargin, childLeft + childWidth,
          lp.topMargin + childHeight);
        break;
      }

      case Gravity.BOTTOM: {
        child.layout(childLeft,
          height - lp.bottomMargin - childHeight,
          childLeft + childWidth,
          height - lp.bottomMargin);
        break;
      }

      case Gravity.CENTER_VERTICAL: {
        int childTop = (height - childHeight) / 2;
        if (childTop < lp.topMargin) {
          childTop = lp.topMargin;
        } else if (childTop + childHeight > height - lp.bottomMargin) {
          childTop = height - lp.bottomMargin - childHeight;
        }
        child.layout(childLeft, childTop, childLeft + childWidth,
          childTop + childHeight);
        break;
      }
    }
  }

  private void slideTopAndBottomLayout(View child, int width, int height) {
    LayoutParams lp = (LayoutParams) child.getLayoutParams();
    final int childWidth = child.getMeasuredWidth();
    final int childHeight = child.getMeasuredHeight();
    int childTop;

    final int hgrav = lp.gravity & Gravity.HORIZONTAL_GRAVITY_MASK;

    if (checkSlideViewAbsoluteGravity(child, Gravity.TOP)) {
      childTop = -childHeight;
      mTopView = child;
    } else {
      childTop = height;
      mBottomView = child;
    }

    switch (hgrav) {
      default:
      case Gravity.LEFT: {
        child.layout(lp.leftMargin, childTop, lp.leftMargin + childWidth, childTop + childHeight);
        break;
      }

      case Gravity.RIGHT: {
        child.layout(width - lp.rightMargin - childWidth, childTop, width - lp.rightMargin,
          childTop + childHeight);
        break;
      }

      case Gravity.CENTER_HORIZONTAL: {
        int childLeft = (width - childWidth) / 2;
        if (childLeft < lp.leftMargin) {
          childLeft = lp.leftMargin;
        } else if (childLeft + childWidth > width - lp.rightMargin) {
          childLeft = width - lp.rightMargin - childWidth;
        }
        child.layout(childLeft, childTop, childLeft + childWidth,
          childTop + childHeight);
        break;
      }
    }
  }

  @Override
  public void requestLayout() {
    if (!mInLayout) {
      super.requestLayout();
    }
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    mFirstLayout = true;
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    mFirstLayout = true;
  }

  boolean isContentView(View child) {
    return ((LayoutParams) child.getLayoutParams()).gravity == Gravity.NO_GRAVITY;
  }

  boolean isSlidingView(View child) {
    return ((LayoutParams) child.getLayoutParams()).gravity != Gravity.NO_GRAVITY;
  }

  boolean checkSlideViewAbsoluteGravity(View slideView, int checkFor) {
    final int absGravity = getSlideViewAbsoluteGravity(slideView);
    return (absGravity & checkFor) == checkFor;
  }

  int getSlideViewAbsoluteGravity(View slideView) {
    final int gravity = ((LayoutParams) slideView.getLayoutParams()).gravity;
    return GravityCompat.getAbsoluteGravity(gravity, ViewCompat.getLayoutDirection(this));
  }


  View findSlideViewWithGravity(int gravity) {
    final int absGravity =
      GravityCompat.getAbsoluteGravity(gravity, ViewCompat.getLayoutDirection(this));
    final int childCount = getChildCount();
    for (int i = 0; i < childCount; i++) {
      final View child = getChildAt(i);
      final int childAbsGravity = getSlideViewAbsoluteGravity(child);
      if (childAbsGravity == absGravity) {
        return child;
      }
    }
    return null;
  }

  private void saveStartLocation() {
    View captureView = null;
    switch (mLockedDirection) {
      case LockDirection.LEFT:
        captureView = mLeftView;
        break;
      case LockDirection.RIGHT:
        captureView = mRightView;
        break;
      case LockDirection.TOP:
        captureView = mTopView;
        break;
      case LockDirection.BOTTOM:
        captureView = mBottomView;
        break;
    }
    if (captureView != null) {
      mCapturedView = captureView;
      mStartLocation.set(captureView.getLeft(), captureView.getTop());
    }
  }

  private class ViewDragCallback implements SlidingDragHelper.Callback {

    @Override
    public void onDragStart() {
      Log.d(TAG, "onDragStart");
    }

    @Override
    public void onDragEnd() {
      Log.d(TAG, "onDragEnd");
      mLocked = false;
    }

    @Override
    public void onDragOffset(int dx, int dy) {
      Log.d("onDragOffset", "drag dx=" + dx + " dy=" + dy);
      if (!mLocked) {
        float xDiff = Math.abs(dx);
        float yDiff = Math.abs(dy);
        if (xDiff > yDiff) {
          mLockedDirection = dx < 0 ? LockDirection.RIGHT : LockDirection.LEFT;
        } else {
          mLockedDirection = dy < 0 ? LockDirection.BOTTOM : LockDirection.TOP;
        }
        mLocked = true;
        Log.d(TAG, "locked direction " + mLockedDirection);
        saveStartLocation();
      }

      if (mContentView == null) {
        Log.d(TAG, "content view is null");
        return;
      }

      switch (mLockedDirection) {
        case LockDirection.LEFT:
          if (mLeftView != null) {
            mDragHelper.dragToByHorizontal(mLeftView, mLeftView.getLeft() + dx, dx);
          }
          break;
        case LockDirection.RIGHT:
          if (mRightView != null) {
            mDragHelper.dragToByHorizontal(mRightView, mRightView.getLeft() + dx, dx);
          }
          break;
        case LockDirection.TOP:
          if (mTopView != null) {
            mDragHelper.dragToByVertical(mTopView, mTopView.getTop() + dy, dy);
          }
          break;
        case LockDirection.BOTTOM:
          if (mBottomView != null) {
            mDragHelper.dragToByVertical(mBottomView, mBottomView.getTop() + dy, dy);
          }
          break;
      }
    }

    @Override
    public int clampX(View dragView, int left, int dx) {
      if (mLockedDirection == LockDirection.LEFT) {
        if (left < -dragView.getWidth()) {
          left = -dragView.getWidth();
        } else if (left > 0) {
          left = 0;
        }
        return left;
      } else if (mLockedDirection == LockDirection.RIGHT) {
        if (left > getWidth()) {
          left = getWidth();
        } else if (left < getWidth() - dragView.getWidth()) {
          left = getWidth() - dragView.getWidth();
        }
        return left;
      }
      return 0;
    }

    @Override
    public int clampY(View dragView, int top, int dy) {
      if (mLockedDirection == LockDirection.TOP) {
        if (top < -dragView.getHeight()) {
          top = -dragView.getHeight();
        } else if (top > 0) {
          top = 0;
        }
        return top;
      } else if (mLockedDirection == LockDirection.BOTTOM) {
        if (top > getHeight()) {
          top = getHeight();
        } else if (top < getHeight() - dragView.getHeight()) {
          top = getHeight() - dragView.getHeight();
        }
        return top;
      }
      return 0;
    }
  }


  @Override
  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
    return new LayoutParams(getContext(), attrs);
  }

  public static class LayoutParams extends ViewGroup.MarginLayoutParams {
    public int gravity = Gravity.NO_GRAVITY;

    public LayoutParams(Context c, AttributeSet attrs) {
      super(c, attrs);
      final TypedArray a = c.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
      this.gravity = a.getInt(0, Gravity.NO_GRAVITY);
      a.recycle();
    }

    public LayoutParams(int width, int height) {
      super(width, height);
    }

    public LayoutParams(MarginLayoutParams source) {
      super(source);
    }
  }

  /**
   * @param enable 是否支持滑动
   */
  public void setEnable(boolean enable) {
    this.mEnable = enable;
  }

  /**
   * @param threshold (0,1],滑动多少比例算滑动成功
   */
  public void setThreshold(float threshold) {
    this.mThreshold = threshold;
  }

  @interface LockDirection {
    int LEFT = 0;
    int RIGHT = 1;
    int TOP = 2;
    int BOTTOM = 3;
  }
}
