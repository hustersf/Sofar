package com.sofar.widget.slide;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.OverScroller;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

public class SlidingDragHelper {

  private static final String TAG = "SlidingDragHelper";

  private Context mContext;
  private final ViewGroup mParentView;
  private boolean mIsDragging;
  private final Callback mCallback;

  private VelocityTracker mVelocityTracker;
  private float mMaxVelocity;
  private float mMinVelocity;
  private int mTouchSlop;
  private OverScroller mScroller;

  private PointF mDownPoint = new PointF();
  private PointF mLastPoint = new PointF();

  private static final Interpolator sInterpolator = new Interpolator() {
    @Override
    public float getInterpolation(float t) {
      t -= 1.0f;
      return t * t * t * t * t + 1.0f;
    }
  };

  public static SlidingDragHelper create(@NonNull ViewGroup forParent, @NonNull Callback callback) {
    return new SlidingDragHelper(forParent.getContext(), forParent, callback);
  }

  private SlidingDragHelper(@NonNull Context context, @NonNull ViewGroup forParent,
    @NonNull Callback callback) {
    mContext = context;
    mParentView = forParent;
    mCallback = callback;
    final ViewConfiguration vc = ViewConfiguration.get(context);
    mTouchSlop = vc.getScaledTouchSlop();
    mMaxVelocity = vc.getScaledMaximumFlingVelocity();
    mMinVelocity = vc.getScaledMinimumFlingVelocity();
    mScroller = new OverScroller(context, sInterpolator);
  }

  public boolean shouldInterceptTouchEvent(@NonNull MotionEvent ev) {
    final int action = ev.getAction();
    if (action == MotionEvent.ACTION_DOWN) {
      cancel();
    }

    if (mVelocityTracker == null) {
      mVelocityTracker = VelocityTracker.obtain();
    }
    mVelocityTracker.addMovement(ev);

    Log.d(TAG, "shouldInterceptTouchEvent=" + action);
    switch (action) {
      case MotionEvent.ACTION_DOWN: {
        float x = ev.getX();
        float y = ev.getY();
        mDownPoint.set(x, y);
        break;
      }
      case MotionEvent.ACTION_MOVE: {
        float x = ev.getX();
        float y = ev.getY();
        float xDiff = Math.abs(x - mDownPoint.x);
        float yDiff = Math.abs(y - mDownPoint.y);
        if (xDiff > mTouchSlop || yDiff > mTouchSlop) {
          if (!mIsDragging) {
            mCallback.onDragStart();
          }
          mIsDragging = true;
        }
        mLastPoint.set(x, y);
        break;
      }
      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP: {
        mIsDragging = false;
        cancel();
        break;
      }
    }
    return mIsDragging;
  }

  public void processTouchEvent(@NonNull MotionEvent ev) {
    final int action = ev.getAction();
    if (action == MotionEvent.ACTION_DOWN) {
      cancel();
    }

    if (mVelocityTracker == null) {
      mVelocityTracker = VelocityTracker.obtain();
    }
    mVelocityTracker.addMovement(ev);
    Log.d(TAG, "processTouchEvent=" + action);

    switch (action) {
      case MotionEvent.ACTION_DOWN: {
        float x = ev.getX();
        float y = ev.getY();
        mDownPoint.set(x, y);
        mLastPoint.set(x, y);
        break;
      }
      case MotionEvent.ACTION_MOVE: {
        float x = ev.getX();
        float y = ev.getY();
        int dx = (int) (x - mLastPoint.x);
        int dy = (int) (y - mLastPoint.y);
        mCallback.onDragOffset(dx, dy);
        mLastPoint.set(x, y);
        break;
      }
      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP: {
        mCallback.onDragEnd();
        mIsDragging = false;
        cancel();
        break;
      }
    }
  }

  public void dragToByHorizontal(View captureView, int left, int dx) {
    int clampedX = left;
    final int oldLeft = captureView.getLeft();
    if (dx != 0) {
      clampedX = mCallback.clampX(captureView, left, dx);
      ViewCompat.offsetLeftAndRight(captureView, clampedX - oldLeft);
    }
  }


  public void dragToByVertical(View captureView, int top, int dy) {
    int clampedY = top;
    final int oldTop = captureView.getTop();
    if (dy != 0) {
      clampedY = mCallback.clampY(captureView, top, dy);
      ViewCompat.offsetTopAndBottom(captureView, clampedY - oldTop);
    }
  }

  public void cancel() {
    clearMotionHistory();

    if (mVelocityTracker != null) {
      mVelocityTracker.recycle();
      mVelocityTracker = null;
    }
  }

  private void clearMotionHistory() {
    mDownPoint.set(0, 0);
    mLastPoint.set(0, 0);
  }

  interface Callback {
    default void onDragStart() {}

    default void onDragEnd() {}

    default void onDragOffset(int dx, int dy) {}

    default int clampX(View dragView, int left, int dx) {
      return 0;
    }

    default int clampY(View dragView, int top, int dy) {
      return 0;
    }
  }

}
