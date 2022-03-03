package com.sofar.widget.nested;

import static androidx.core.view.ViewCompat.TYPE_NON_TOUCH;
import static androidx.core.view.ViewCompat.TYPE_TOUCH;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.webkit.WebView;
import android.widget.OverScroller;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild3;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;

import com.sofar.widget.BuildConfig;

public class NestedWebView extends WebView implements NestedScrollingChild3 {
  private static final String TAG = "NestedWebView";

  private NestedScrollingChildHelper mScrollingChildHelper;
  private final int[] mScrollOffset = new int[2];
  private final int[] mNestedOffsets = new int[2];
  final int[] mReusableIntPair = new int[2];

  private List<OnScrollListener> mScrollListeners;
  private int mScrollState = SCROLL_STATE_IDLE;
  public static final int SCROLL_STATE_IDLE = 0;
  public static final int SCROLL_STATE_DRAGGING = 1;
  public static final int SCROLL_STATE_SETTLING = 2;

  private VelocityTracker mVelocityTracker;
  private int mLastTouchY;

  private final int mTouchSlop;
  private final float DENSITY;
  private final int mMinFlingVelocity;
  private final int mMaxFlingVelocity;

  private int mJsCallWebViewContentHeight;
  private int mWebViewContentHeight;

  final ViewFlinger mViewFlinger = new ViewFlinger();
  static final Interpolator sQuinticInterpolator = new Interpolator() {
    @Override
    public float getInterpolation(float t) {
      t -= 1.0f;
      return t * t * t * t * t + 1.0f;
    }
  };

  public NestedWebView(Context context) {
    this(context, null);
  }

  public NestedWebView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public NestedWebView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    final ViewConfiguration vc = ViewConfiguration.get(context);
    mTouchSlop = vc.getScaledTouchSlop();
    DENSITY = context.getResources().getDisplayMetrics().density;
    mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
    mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();

    setNestedScrollingEnabled(true);
  }

  public void setJsCallWebViewContentHeight(int webViewContentHeight) {
    if (webViewContentHeight > 0 && webViewContentHeight != mJsCallWebViewContentHeight) {
      mJsCallWebViewContentHeight = webViewContentHeight;
      if (mJsCallWebViewContentHeight < getHeight()) {
        // 内部高度<控件高度时,调整控件高度为内容高度
        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.height = mJsCallWebViewContentHeight;
        setLayoutParams(lp);
      }
    }
  }

  public int getWebViewContentHeight() {
    if (mWebViewContentHeight == 0) {
      mWebViewContentHeight = mJsCallWebViewContentHeight;
    }

    if (mWebViewContentHeight == 0) {
      mWebViewContentHeight = (int) (getContentHeight() * DENSITY);
    }
    return mWebViewContentHeight;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    Log.d(TAG, "height=" + getMeasuredHeight());
  }

  /**
   * {@link androidx.recyclerview.widget.RecyclerView#onTouchEvent(MotionEvent)}
   */
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    final int action = event.getActionMasked();

    if (mVelocityTracker == null) {
      mVelocityTracker = VelocityTracker.obtain();
    }

    if (action == MotionEvent.ACTION_DOWN) {
      mNestedOffsets[0] = mNestedOffsets[1] = 0;
    }
    boolean eventAddedToVelocityTracker = false;
    final MotionEvent vtev = MotionEvent.obtain(event);
    vtev.offsetLocation(mNestedOffsets[0], mNestedOffsets[1]);

    switch (action) {
      case MotionEvent.ACTION_DOWN:
        mLastTouchY = (int) (event.getY() + 0.5f);
        int nestedScrollAxis = ViewCompat.SCROLL_AXIS_VERTICAL;
        startNestedScroll(nestedScrollAxis, TYPE_TOUCH);
        break;
      case MotionEvent.ACTION_MOVE:
        final int y = (int) (event.getY() + 0.5f);
        int dy = mLastTouchY - y;

        if (mScrollState != SCROLL_STATE_DRAGGING) {
          boolean startScroll = false;
          if (dy > 0) {
            dy = Math.max(0, dy - mTouchSlop);
          } else {
            dy = Math.min(0, dy + mTouchSlop);
          }
          if (dy != 0) {
            startScroll = true;
          }
          if (startScroll) {
            setScrollState(SCROLL_STATE_DRAGGING);
          }
        }

        if (mScrollState == SCROLL_STATE_DRAGGING) {
          mReusableIntPair[0] = 0;
          mReusableIntPair[1] = 0;
          if (dispatchNestedPreScroll(0, dy, mReusableIntPair, mScrollOffset, TYPE_TOUCH)) {
            dy -= mReusableIntPair[1];
            // Updated the nested offsets
            mNestedOffsets[0] += mScrollOffset[0];
            mNestedOffsets[1] += mScrollOffset[1];
            // Scroll has initiated, prevent parents from intercepting
            getParent().requestDisallowInterceptTouchEvent(true);
          }

          mLastTouchY = y - mScrollOffset[1];
          if (scrollByInternal(dy)) {
            getParent().requestDisallowInterceptTouchEvent(true);
          }
          //屏蔽WebView本身的滑动，滑动事件自己处理
          event.setAction(MotionEvent.ACTION_CANCEL);
        }
        break;
      case MotionEvent.ACTION_UP:
        if (mScrollState == SCROLL_STATE_DRAGGING) {
          event.setAction(MotionEvent.ACTION_CANCEL);
        }
        mVelocityTracker.addMovement(vtev);
        eventAddedToVelocityTracker = true;
        mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
        final int yvel = (int) -mVelocityTracker.getYVelocity();
        if (!fling(yvel)) {
          setScrollState(SCROLL_STATE_IDLE);
        }
        resetScroll();
        break;
      case MotionEvent.ACTION_CANCEL:
        cancelScroll();
        break;
    }

    if (!eventAddedToVelocityTracker) {
      mVelocityTracker.addMovement(vtev);
    }
    vtev.recycle();

    super.onTouchEvent(event);
    return true;
  }


  boolean scrollByInternal(int y) {
    int unconsumedX = 0;
    int unconsumedY = 0;
    int consumedX = 0;
    int consumedY = 0;

    //自己消费
    mReusableIntPair[0] = 0;
    mReusableIntPair[1] = 0;
    scrollStep(y, mReusableIntPair);
    consumedY = mReusableIntPair[1];
    unconsumedY = y - consumedY;

    //传递给父View消费
    mReusableIntPair[0] = 0;
    mReusableIntPair[1] = 0;
    dispatchNestedScroll(consumedX, consumedY, unconsumedX, unconsumedY, mScrollOffset,
      TYPE_TOUCH, mReusableIntPair);
    unconsumedY -= mReusableIntPair[1];
    boolean consumedNestedScroll = mReusableIntPair[0] != 0 || mReusableIntPair[1] != 0;

    // Update the last touch co-ords, taking any scroll offset into account
    mLastTouchY -= mScrollOffset[1];
    mNestedOffsets[0] += mScrollOffset[0];
    mNestedOffsets[1] += mScrollOffset[1];

    return consumedNestedScroll || consumedX != 0 || consumedY != 0;
  }

  void scrollStep(int y, @Nullable int[] consumed) {
    final int oldScrollY = getScrollY();
    int preScroll = y + oldScrollY;
    final int range = getWebViewContentHeight() - getHeight();
    if (preScroll < 0) {
      preScroll = 0;
    }
    if (preScroll > range) {
      preScroll = range;
    }
    int dy = preScroll - oldScrollY;
    scrollBy(0, dy);
    if (consumed != null) {
      consumed[1] = dy;
    }
  }

  public boolean fling(int velocityY) {
    int velocityX = 0;
    if (Math.abs(velocityY) < mMinFlingVelocity) {
      velocityY = 0;
    }

    if (velocityY == 0) {
      // If we don't have any velocity, return false
      return false;
    }

    if (!dispatchNestedPreFling(velocityX, velocityY)) {
      final boolean canScroll = true;
      dispatchNestedFling(velocityX, velocityY, canScroll);

      if (canScroll) {
        int nestedScrollAxis = ViewCompat.SCROLL_AXIS_VERTICAL;
        startNestedScroll(nestedScrollAxis, TYPE_NON_TOUCH);

        velocityX = Math.max(-mMaxFlingVelocity, Math.min(velocityX, mMaxFlingVelocity));
        velocityY = Math.max(-mMaxFlingVelocity, Math.min(velocityY, mMaxFlingVelocity));
        mViewFlinger.fling(velocityX, velocityY);
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean canScrollVertically(int direction) {
    final int range = getWebViewContentHeight() - getHeight();
    if (range <= 0) {
      return false;
    }

    final int offset = getScrollY();
    if (direction < 0) {
      return offset > 0;
    } else {
      return offset < range - 1;
    }
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    stopScroll();
  }

  public void stopScroll() {
    setScrollState(SCROLL_STATE_IDLE);
    stopScrollerInternal();
  }

  private void stopScrollerInternal() {
    mViewFlinger.stop();
  }

  public int getScrollState() {
    return mScrollState;
  }

  void setScrollState(int state) {
    if (state == mScrollState) {
      return;
    }
    if (BuildConfig.DEBUG) {
      Log.d(TAG, "setting scroll state to " + state + " from " + mScrollState);
    }
    mScrollState = state;
    if (state != SCROLL_STATE_SETTLING) {
      stopScrollerInternal();
    }
    dispatchOnScrollStateChanged(mScrollState);
  }

  private void resetScroll() {
    if (mVelocityTracker != null) {
      mVelocityTracker.clear();
    }
    stopNestedScroll(TYPE_TOUCH);
  }

  private void cancelScroll() {
    resetScroll();
    setScrollState(SCROLL_STATE_IDLE);
  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    if (mScrollListeners != null) {
      for (int i = mScrollListeners.size() - 1; i >= 0; i--) {
        int dx = l - oldl;
        int dy = t - oldt;
        mScrollListeners.get(i).onScrolled(dx, dy);
      }
    }
  }

  void dispatchOnScrollStateChanged(int state) {
    if (mScrollListeners != null) {
      for (int i = mScrollListeners.size() - 1; i >= 0; i--) {
        mScrollListeners.get(i).onScrollStateChanged(state);
      }
    }
  }

  @Override
  public int computeVerticalScrollOffset() {
    return super.computeVerticalScrollOffset();
  }

  @Override
  public int computeHorizontalScrollExtent() {
    return super.computeHorizontalScrollExtent();
  }

  @Override
  public int computeVerticalScrollRange() {
    return super.computeVerticalScrollRange();
  }

  // NestedScrollingChild3
  @Override
  public void dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
    int dyUnconsumed, @Nullable int[] offsetInWindow, int type, @NonNull int[] consumed) {
    getScrollingChildHelper().dispatchNestedScroll(dxConsumed, dyConsumed,
      dxUnconsumed, dyUnconsumed, offsetInWindow, type, consumed);
  }

  // NestedScrollingChild2
  @Override
  public boolean startNestedScroll(int axes, int type) {
    return getScrollingChildHelper().startNestedScroll(axes, type);
  }

  @Override
  public void stopNestedScroll(int type) {
    getScrollingChildHelper().stopNestedScroll(type);
  }

  @Override
  public boolean hasNestedScrollingParent(int type) {
    return getScrollingChildHelper().hasNestedScrollingParent(type);
  }

  @Override
  public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
    int dyUnconsumed, @Nullable int[] offsetInWindow, int type) {
    return getScrollingChildHelper().dispatchNestedScroll(dxConsumed, dyConsumed,
      dxUnconsumed, dyUnconsumed, offsetInWindow, type);
  }

  @Override
  public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed,
    @Nullable int[] offsetInWindow, int type) {
    return getScrollingChildHelper().dispatchNestedPreScroll(dx, dy,
      consumed, offsetInWindow, type);
  }

  // NestedScrollingChild
  @Override
  public void setNestedScrollingEnabled(boolean enabled) {
    getScrollingChildHelper().setNestedScrollingEnabled(enabled);
  }

  @Override
  public boolean isNestedScrollingEnabled() {
    return getScrollingChildHelper().isNestedScrollingEnabled();
  }

  @Override
  public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
    return getScrollingChildHelper().dispatchNestedFling(velocityX, velocityY, consumed);
  }

  @Override
  public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
    return getScrollingChildHelper().dispatchNestedPreFling(velocityX, velocityY);
  }

  private NestedScrollingChildHelper getScrollingChildHelper() {
    if (mScrollingChildHelper == null) {
      mScrollingChildHelper = new NestedScrollingChildHelper(this);
    }
    return mScrollingChildHelper;
  }

  /**
   * {@link androidx.recyclerview.widget.RecyclerView#fling(int, int)}
   */
  class ViewFlinger implements Runnable {

    private int mLastFlingX;
    private int mLastFlingY;
    OverScroller mOverScroller;
    Interpolator mInterpolator = sQuinticInterpolator;

    // When set to true, postOnAnimation callbacks are delayed until the run method completes
    private boolean mEatRunOnAnimationRequest = false;

    // Tracks if postAnimationCallback should be re-attached when it is done
    private boolean mReSchedulePostAnimationCallback = false;

    ViewFlinger() {
      mOverScroller = new OverScroller(getContext(), sQuinticInterpolator);
    }

    @Override
    public void run() {
      mReSchedulePostAnimationCallback = false;
      mEatRunOnAnimationRequest = true;

      final OverScroller scroller = mOverScroller;
      if (scroller.computeScrollOffset()) {
        final int x = scroller.getCurrX();
        final int y = scroller.getCurrY();
        int unconsumedX = x - mLastFlingX;
        int unconsumedY = y - mLastFlingY;
        mLastFlingX = x;
        mLastFlingY = y;
        int consumedX = 0;
        int consumedY = 0;

        // Nested Pre Scroll
        mReusableIntPair[0] = 0;
        mReusableIntPair[1] = 0;
        if (dispatchNestedPreScroll(unconsumedX, unconsumedY, mReusableIntPair, null,
          TYPE_NON_TOUCH)) {
          unconsumedX -= mReusableIntPair[0];
          unconsumedY -= mReusableIntPair[1];
        }

        // Local Scroll
        mReusableIntPair[0] = 0;
        mReusableIntPair[1] = 0;
        scrollStep(unconsumedY, mReusableIntPair);
        consumedX = mReusableIntPair[0];
        consumedY = mReusableIntPair[1];
        unconsumedX -= consumedX;
        unconsumedY -= consumedY;

        // Nested Post Scroll
        mReusableIntPair[0] = 0;
        mReusableIntPair[1] = 0;
        dispatchNestedScroll(consumedX, consumedY, unconsumedX, unconsumedY, null,
          TYPE_NON_TOUCH, mReusableIntPair);
        unconsumedX -= mReusableIntPair[0];
        unconsumedY -= mReusableIntPair[1];

        boolean scrollerFinishedX = scroller.getCurrX() == scroller.getFinalX();
        boolean scrollerFinishedY = scroller.getCurrY() == scroller.getFinalY();
        final boolean doneScrolling = scroller.isFinished()
          || ((scrollerFinishedX || unconsumedX != 0)
          && (scrollerFinishedY || unconsumedY != 0));
        if (!doneScrolling) {
          postOnAnimation();
        }
      }

      mEatRunOnAnimationRequest = false;
      if (mReSchedulePostAnimationCallback) {
        internalPostOnAnimation();
      } else {
        setScrollState(SCROLL_STATE_IDLE);
        stopNestedScroll(TYPE_NON_TOUCH);
      }
    }

    public void fling(int velocityX, int velocityY) {
      setScrollState(SCROLL_STATE_SETTLING);
      mLastFlingX = mLastFlingY = 0;
      if (mInterpolator != sQuinticInterpolator) {
        mInterpolator = sQuinticInterpolator;
        mOverScroller = new OverScroller(getContext(), sQuinticInterpolator);
      }
      mOverScroller.fling(0, 0, velocityX, velocityY,
        Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
      postOnAnimation();
    }

    void postOnAnimation() {
      if (mEatRunOnAnimationRequest) {
        mReSchedulePostAnimationCallback = true;
      } else {
        internalPostOnAnimation();
      }
    }

    private void internalPostOnAnimation() {
      removeCallbacks(this);
      ViewCompat.postOnAnimation(NestedWebView.this, this);
    }

    public void stop() {
      removeCallbacks(this);
      mOverScroller.abortAnimation();
    }
  }

  public void addOnScrollListener(@NonNull OnScrollListener listener) {
    if (mScrollListeners == null) {
      mScrollListeners = new ArrayList<>();
    }
    mScrollListeners.add(listener);
  }

  public void removeOnScrollListener(@NonNull OnScrollListener listener) {
    if (mScrollListeners != null) {
      mScrollListeners.remove(listener);
    }
  }

  /**
   * 监听滚动变化
   */
  public abstract static class OnScrollListener {
    public void onScrollStateChanged(int newState) {}

    public void onScrolled(int dx, int dy) {}
  }

}

