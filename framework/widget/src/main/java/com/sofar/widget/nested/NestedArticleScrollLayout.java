package com.sofar.widget.nested;

import static androidx.core.view.ViewCompat.TYPE_NON_TOUCH;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.OverScroller;
import android.widget.Scroller;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;

import com.sofar.utility.reflect.FieldUtil;

/**
 * 文章详情页嵌套滑动升级版
 * <p>
 * 支持 {@link NestedScrollView} 所有特性
 * 1.支持外层嵌套下拉刷新控件
 * 2.支持 WebView + RecyclerView  中间插入 任意普通控件
 * <p>
 * 注意：
 * 不支持 嵌套多个 RecyclerView
 * RecyclerView 需要设置确定高度，否则复用会失效(NestedScrollView特性)
 */
public class NestedArticleScrollLayout extends NestedScrollView {
  private static String TAG = "NestedArticleScroll";

  private static final int DEFAULT_DURATION = 250;

  private final int[] mParentScrollConsumed = new int[2];
  private final int[] mScrollConsumed = new int[2];

  private int mScrollThreshold;

  private final int mMinFlingVelocity;
  private final int mMaxFlingVelocity;

  private NestedScrollingChildHelper mSuperClsChildHelper;
  private View mTargetChild;

  private int mTrackStartVelocityY;
  ViewScroller mChildTrackFlinger = new ViewScroller();
  ViewScroller mFlinger = new ViewScroller();
  ViewScroller mScroller = new ViewScroller();
  static final Interpolator sQuinticInterpolator = new Interpolator() {
    @Override
    public float getInterpolation(float t) {
      t -= 1.0f;
      return t * t * t * t * t + 1.0f;
    }
  };

  public NestedArticleScrollLayout(@NonNull Context context) {
    this(context, null);
  }

  public NestedArticleScrollLayout(@NonNull Context context,
    @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public NestedArticleScrollLayout(@NonNull Context context, @Nullable AttributeSet attrs,
    int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    injectChildHelper();

    final ViewConfiguration vc = ViewConfiguration.get(context);
    mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
    mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
  }

  /**
   * 通过反射获取父类的mChildHelper
   */
  private void injectChildHelper() {
    try {
      mSuperClsChildHelper = (NestedScrollingChildHelper) FieldUtil.readField(this, "mChildHelper");
      Log.d(TAG, "injectChildHelper id=" + mSuperClsChildHelper.toString());
    } catch (Exception e) {
      Log.e(TAG, "injectChildHelper error=" + e.toString());
    }
  }


  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);
    mScrollThreshold = computeVerticalScrollRange() - getMeasuredHeight();
    if (mScrollThreshold < 0) {
      mScrollThreshold = 0;
    }
  }

  public void scrollToTarget(@NonNull View target) {
    scrollToTarget(target, DEFAULT_DURATION);
  }

  /**
   * 滑动指定View到顶部
   *
   * @param target   孙子View
   * @param duration 滑动时间
   */
  public void scrollToTarget(@NonNull View target, int duration) {
    mFlinger.stop();

    View child = getChildAt(0);
    if (child instanceof ViewGroup) {
      int top = target.getTop() - getScrollY();
      mScroller.startScroll(0, top, duration);

      ViewGroup parent = (ViewGroup) child;
      postDelayed(() -> {
        int targetIndex = parent.indexOfChild(target);
        for (int i = 0; i < targetIndex; i++) {
          View grandson = parent.getChildAt(i);
          if (grandson instanceof NestedLinkScrollChild) {
            ((NestedLinkScrollChild) grandson).scrollToBottom();
          }
        }

        for (int i = targetIndex; i < parent.getChildCount(); i++) {
          View grandson = parent.getChildAt(i);
          if (grandson instanceof NestedLinkScrollChild) {
            ((NestedLinkScrollChild) grandson).scrollToTop();
          }
        }
      }, duration);
    }
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    if (ev.getAction() == MotionEvent.ACTION_DOWN) {
      Log.d(TAG, "dispatchTouchEvent ACTION_DOWN y=" + ev.getY());
      mTargetChild = findTargetView(ev);
    }
    return super.dispatchTouchEvent(ev);
  }

  private View findTargetView(MotionEvent ev) {
    float y = ev.getY();
    View target = null;
    View child = getChildAt(0);
    if (child instanceof ViewGroup) {
      ViewGroup parent = (ViewGroup) child;
      for (int i = 0; i < parent.getChildCount(); i++) {
        View grandson = parent.getChildAt(i);
        if (grandson == null) {
          break;
        }

        int top = grandson.getTop() - getScrollY();
        int bottom = grandson.getBottom() - getScrollY();
        if (y >= top && y <= bottom) {
          target = grandson;
          Log.d(TAG, "find target view=" + grandson.toString());
        }
      }
    }

    return target;
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    boolean intercepted = super.onInterceptTouchEvent(ev);
    Log.d(TAG, "intercepted=" + intercepted + " ev=" + ev.getAction());
    return intercepted;
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    if (ev.getAction() == MotionEvent.ACTION_DOWN) {
      Log.d(TAG, "onTouchEvent ACTION_DOWN y=" + ev.getY());
    }
    return super.onTouchEvent(ev);
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    mChildTrackFlinger.stop();
    mFlinger.stop();
  }

  // NestedScrollingParent3

  @Override
  public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed,
    int dyUnconsumed, int type, @NonNull int[] consumed) {
    onNestedScrollInternal(target, dyUnconsumed, type, consumed);
  }

  /**
   * 重写 onNestedScroll 的逻辑，保证子View之间的 fling操作连贯性
   */
  private void onNestedScrollInternal(@NonNull View target, int dyUnconsumed, int type,
    @Nullable int[] consumed) {
    final int oldScrollY = getScrollY();
    scrollBy(0, dyUnconsumed);
    final int myConsumed = getScrollY() - oldScrollY;

    if (consumed != null) {
      consumed[1] += myConsumed;
    }
    final int myUnconsumed = dyUnconsumed - myConsumed;
    Log.d(TAG, "onNestedScroll parent start scroll myUnconsumed=" + myUnconsumed);

    boolean find = findNestedLinkChildFling(myUnconsumed, target);
    if (!find && mSuperClsChildHelper != null) {
      mSuperClsChildHelper.dispatchNestedScroll(0, myConsumed,
        0, myUnconsumed, null, type, consumed);
    }
  }

  /**
   * 解决子View之间 fling 操作戛然而止的问题
   *
   * @param target
   * @param dyUnconsumed >0 上滑(手指从下到上)
   */
  private boolean findNestedLinkChildFling(int dyUnconsumed, @NonNull View target) {
    View parent = getChildAt(0);
    if (dyUnconsumed == 0) {
      return false;
    }

    if (parent instanceof ViewGroup) {
      View childView;
      if (dyUnconsumed > 0) {
        childView = findNextNestedLinkChild((ViewGroup) parent, target);
      } else {
        childView = findPreNestedLinkChild((ViewGroup) parent, target);
      }

      if (childView == null) {
        return false;
      }

      int curVelocity = (int) mChildTrackFlinger.mScroller.getCurrVelocity();
      if (dyUnconsumed < 0) {
        curVelocity = -curVelocity;
      }

      if (curVelocity == 0) {
        /**
         * 偶现问题说明：
         * 获取当前速度 float 对应的值是NaN，转换成int 就是0
         * 当curVelocity==0时， fling 仍然是戛然而止
         * 29的SDK版本 OverScroller 无此问题（复制了一份29的源码）
         * 测试手机是28的SDK才有此问题
         *
         * 解决方案 {@link NestedArticleDetailLayout.ViewFlinger#mScroller}
         * 由{@link Scroller} 替换掉 {@link OverScroller}
         */
        Log.e(TAG, "findNestedLinkChildScroll curVelocity==0 issue");
      }

      if (Math.abs(mTrackStartVelocityY) < Math.abs(curVelocity)) {
        /**
         * 当前速度>起始速度{@link Scroller#Scroller(Context, Interpolator, boolean)}
         * 第三个参数 flywheel 默认是  ture，会累计之前的速度
         * {@link Scroller#fling(int, int, int, int, int, int, int, int)}
         */
        Log.i(TAG, "findNestedLinkChildScroll curVelocity(" + curVelocity
          + ") > mTrackStartVelocityY(" + mTrackStartVelocityY + ")");
      }

      if (childView instanceof NestedLinkScrollChild) {
        Log.d(TAG, "findNestedLinkChildScroll child fling velocityY=" + curVelocity
          + " child=" + childView.getClass().getSimpleName());
        ((NestedLinkScrollChild) childView).fling(curVelocity);
        return true;
      }
    }
    return false;
  }

  /**
   * 向上寻找可滑动的子View
   */
  @Nullable
  private View findPreNestedLinkChild(@NonNull ViewGroup parent, @NonNull View target) {
    View childView = null;
    boolean findTarget = false;
    for (int i = parent.getChildCount() - 1; i >= 0; i--) {
      View child = parent.getChildAt(i);

      if (findTarget && canNestedLinkScroll(child)) {
        childView = child;
        break;
      }

      if (child == target) {
        findTarget = true;
      }

    }
    return childView;
  }


  /**
   * 向下寻找可滑动的子View
   */
  @Nullable
  private View findNextNestedLinkChild(@NonNull ViewGroup parent, @NonNull View target) {
    View childView = null;
    boolean findTarget = false;
    for (int i = 0; i < parent.getChildCount(); i++) {
      View child = parent.getChildAt(i);

      if (findTarget && canNestedLinkScroll(child)) {
        childView = child;
        break;
      }

      if (child == target) {
        findTarget = true;
      }

    }
    return childView;
  }

  private boolean canNestedLinkScroll(@Nullable View view) {
    if (view == null || view.getVisibility() == GONE) {
      return false;
    }
    return view instanceof NestedLinkScrollChild;
  }

  /**
   * 重写 fling 逻辑，保证fling操作可以延续到下一个View
   */
  @Override
  public void fling(int velocityY) {
    // super.fling(velocityY);
    mFlinger.fling(0, velocityY);
    trackChildFling(velocityY);
  }

  // NestedScrollingParent2

  @Override
  public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes,
    int type) {
    return super.onStartNestedScroll(child, target, axes, type);
  }

  @Override
  public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes,
    int type) {
    super.onNestedScrollAccepted(child, target, axes, type);
  }

  @Override
  public void onStopNestedScroll(@NonNull View target, int type) {
    super.onStopNestedScroll(target, type);
  }

  @Override
  public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed,
    int dyUnconsumed, int type) {
    onNestedScrollInternal(target, dyUnconsumed, type, null);
  }


  /**
   * 重写 onNestedPreScroll 逻辑，保证WebView先滑，然后父View滑动,最后才是RecyclerView滑动
   * <p>
   * 参考 SwipeRefreshLayout 的 onNestedPreScroll
   * 当前父View 优先消费，未消费的在传递给上一层父View
   * <p>
   * consumed 此方法中的 consumed 非累计,每次都会重置为0
   * {@link NestedScrollingChildHelper#dispatchNestedPreScroll(int, int, int[], int[])}
   */
  @Override
  public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed,
    int type) {
    if (getScrollY() > 0 && getScrollY() < mScrollThreshold) {
      Log.d(TAG, "onNestedPreScroll parent start scroll");
      final int oldScrollY = getScrollY();
      scrollBy(0, dy);
      final int myConsumed = getScrollY() - oldScrollY;
      consumed[1] = myConsumed;
    }

    final int[] parentConsumed = mParentScrollConsumed;
    super.onNestedPreScroll(target, dx, dy - consumed[1], parentConsumed, type);
    consumed[1] += parentConsumed[1];
  }

  @Override
  public int computeVerticalScrollRange() {
    final int count = getChildCount();
    final int parentSpace = getHeight() - getPaddingBottom() - getPaddingTop();
    if (count == 0) {
      return parentSpace;
    }

    View child = getChildAt(0);
    NestedScrollView.LayoutParams lp = (LayoutParams) child.getLayoutParams();
    int scrollRange = child.getBottom() + lp.bottomMargin;
    final int scrollY = getScrollY();
    final int overscrollBottom = Math.max(0, scrollRange - parentSpace);
    if (scrollY < 0) {
      scrollRange -= scrollY;
    } else if (scrollY > overscrollBottom) {
      scrollRange += scrollY - overscrollBottom;
    }

    return scrollRange;
  }

  // NestedScrollingParent

  @Override
  public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
    return super.onStartNestedScroll(child, target, nestedScrollAxes);
  }

  @Override
  public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
    super.onNestedScrollAccepted(child, target, nestedScrollAxes);
  }

  @Override
  public void onStopNestedScroll(View target) {
    super.onStopNestedScroll(target);
  }

  @Override
  public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed,
    int dyUnconsumed) {
    onNestedScrollInternal(target, dyUnconsumed, ViewCompat.TYPE_TOUCH, null);
  }

  @Override
  public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
    super.onNestedPreScroll(target, dx, dy, consumed);
  }

  @Override
  public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
    return super.onNestedFling(target, velocityX, velocityY, consumed);
  }

  @Override
  public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
    trackChildFling((int) velocityY);
    return super.onNestedPreFling(target, velocityX, velocityY);
  }

  private void trackChildFling(int velocityY) {
    velocityY = Math.max(-mMaxFlingVelocity, Math.min(velocityY, mMaxFlingVelocity));
    Log.d(TAG, "trackChildFling velocityY=" + velocityY);
    mTrackStartVelocityY = velocityY;
    mChildTrackFlinger.trackFling(0, velocityY);
  }

  @Override
  public int getNestedScrollAxes() {
    return super.getNestedScrollAxes();
  }

  /**
   * {@link androidx.recyclerview.widget.RecyclerView#fling(int, int)}
   * {@link NestedScrollView#computeScroll()}
   */
  class ViewScroller implements Runnable {

    private static final int TYPE_FLING = 0;
    private static final int TYPE_SCROLL = 1;

    private int mLastY;
    final Scroller mScroller;
    private int mType;

    //true，表示仅仅跟踪fling值的变化，而不去真正滚动
    boolean mTrackFling;

    // When set to true, postOnAnimation callbacks are delayed until the run method completes
    private boolean mEatRunOnAnimationRequest = false;

    // Tracks if postAnimationCallback should be re-attached when it is done
    private boolean mReSchedulePostAnimationCallback = false;

    ViewScroller() {
      mScroller = new Scroller(getContext(), sQuinticInterpolator);
    }

    @Override
    public void run() {
      mReSchedulePostAnimationCallback = false;
      mEatRunOnAnimationRequest = true;

      final Scroller scroller = mScroller;
      if (scroller.computeScrollOffset()) {
        final int y = scroller.getCurrY();
        int unconsumedY = y - mLastY;
        mLastY = y;

        if (!mTrackFling) {
          // Nested Scrolling Pre Pass
          mScrollConsumed[1] = 0;
          dispatchNestedPreScroll(0, unconsumedY, mScrollConsumed, null,
            ViewCompat.TYPE_NON_TOUCH);
          unconsumedY -= mScrollConsumed[1];

          if (unconsumedY != 0) {
            // Internal Scroll
            final int oldScrollY = getScrollY();
            scrollBy(0, unconsumedY);
            final int scrolledByMe = getScrollY() - oldScrollY;
            unconsumedY -= scrolledByMe;

            // Nested Scrolling Post Pass
            mScrollConsumed[1] = 0;
            dispatchNestedScroll(0, scrolledByMe, 0, unconsumedY, null,
              ViewCompat.TYPE_NON_TOUCH, mScrollConsumed);
            unconsumedY -= mScrollConsumed[1];
            if (unconsumedY != 0 && mTargetChild != null && mType == TYPE_FLING) {
              Log.d(TAG, "NestedArticleScrollLayout fling unconsumedY=" + unconsumedY);
              findNestedLinkChildFling(unconsumedY, mTargetChild);
            }
          }
        }

        boolean scrollerFinishedY = scroller.getCurrY() == scroller.getFinalY();
        boolean scrollerFinished = scroller.isFinished();
        final boolean doneScrolling;
        if (mTrackFling) {
          doneScrolling = scrollerFinished || scrollerFinishedY;
        } else {
          doneScrolling = scrollerFinished || (scrollerFinishedY || unconsumedY != 0);
        }
        if (!doneScrolling) {
          postOnAnimation();
        }
      }

      mEatRunOnAnimationRequest = false;
      if (mReSchedulePostAnimationCallback) {
        internalPostOnAnimation();
      } else {
        internalStop();
      }
    }

    public void trackFling(int velocityX, int velocityY) {
      mTrackFling = true;
      fling(velocityX, velocityY);
    }

    public void fling(int velocityX, int velocityY) {
      mLastY = 0;
      mType = TYPE_FLING;
      mScroller.fling(0, 0, velocityX, velocityY,
        Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
      postOnAnimation();
    }

    public void startScroll(int dx, int dy, int duration) {
      mLastY = 0;
      mType = TYPE_SCROLL;
      mScroller.startScroll(0, 0, dx, dy, duration);
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
      ViewCompat.postOnAnimation(NestedArticleScrollLayout.this, this);
    }

    private void internalStop() {
      stop();
      if (!mTrackFling) {
        stopNestedScroll(TYPE_NON_TOUCH);
      }
    }

    public void stop() {
      removeCallbacks(this);
      mScroller.abortAnimation();
    }
  }
}
