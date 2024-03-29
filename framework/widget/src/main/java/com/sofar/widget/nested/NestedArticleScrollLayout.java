package com.sofar.widget.nested;

import static androidx.core.view.ViewCompat.TYPE_NON_TOUCH;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
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
import com.sofar.widget.BuildConfig;

/**
 * 文章详情页嵌套滑动升级版
 * <p>
 * 支持 {@link NestedScrollView} 所有特性
 * 1.支持外层嵌套下拉刷新控件
 * 2.支持 WebView + RecyclerView  中间插入 任意普通控件
 * <p>
 * 注意：
 * 1.不支持 嵌套多个 RecyclerView
 * 2.RecyclerView 需要设置确定高度，否则复用会失效(NestedScrollView特性)
 * 配合 {@link NestedArticleScrollChildLayout} 使用，可以解决上述问题
 */
public class NestedArticleScrollLayout extends NestedScrollView {
  private static String TAG = "NestedArticleScroll";
  private static String TAG2 = "NestedScrollBar";

  private static final int DEFAULT_DURATION = 250;

  private final int[] mParentScrollConsumed = new int[2];
  private final int[] mScrollConsumed = new int[2];
  private int mScrollThreshold;

  private VelocityTracker mVelocityTracker;
  private int mLastTouchY;

  private List<OnScrollListener> mScrollListeners;
  private int mScrollState = SCROLL_STATE_IDLE;
  public static final int SCROLL_STATE_IDLE = 0;
  public static final int SCROLL_STATE_DRAGGING = 1;
  public static final int SCROLL_STATE_SETTLING = 2;

  private final int mMinFlingVelocity;
  private final int mMaxFlingVelocity;
  private final int mTouchSlop;

  private NestedScrollingChildHelper mSuperClsChildHelper;
  private View mTargetChild;
  private List<NestedLinkScrollChild> mNestedChildren = new ArrayList<>();

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

  NestedLinkScrollChild.OnNestedScrollListener mNestedScrollListener =
    new NestedLinkScrollChild.OnNestedScrollListener() {
      @Override
      public void onNestedScrollStateChanged(@NonNull View target, int newState) {
        Log.i(TAG, target.getClass().getSimpleName() + " state=" + newState);
        dispatchOnNestedScrollStateChanged(target, newState);
      }

      @Override
      public void onNestedScrolled(@NonNull View target, int dx, int dy) {
        awakenScrollBars();
        dispatchOnNestedScrolled(target, dx, dy);
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
    mTouchSlop = vc.getScaledTouchSlop();
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
  protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed,
    int parentHeightMeasureSpec, int heightUsed) {
    if (child instanceof NestedArticleScrollChildLayout) {
      final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
      final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
        getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin
          + widthUsed, lp.width);

      int space = getPaddingTop() + getPaddingBottom()
        + lp.topMargin + lp.bottomMargin + heightUsed;
      int childHeight = MeasureSpec.getSize(parentHeightMeasureSpec) - space;
      final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
        childHeight, MeasureSpec.UNSPECIFIED);
      child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    } else {
      super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed,
        parentHeightMeasureSpec, heightUsed);
    }
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);
    mScrollThreshold = computeChildHeight() - getMeasuredHeight();
    if (mScrollThreshold < 0) {
      mScrollThreshold = 0;
    }
    setNestedScrollListener();
  }

  /**
   * 设置子View的滑动状态监听
   */
  private void setNestedScrollListener() {
    mNestedChildren.clear();
    View child = getChildAt(0);
    if (child instanceof ViewGroup) {
      ViewGroup parent = (ViewGroup) child;
      for (int i = 0; i < parent.getChildCount(); i++) {
        View grandson = parent.getChildAt(i);
        if (grandson instanceof NestedLinkScrollChild) {
          ((NestedLinkScrollChild) grandson).setOnNestedScrollListener(mNestedScrollListener);
          mNestedChildren.add((NestedLinkScrollChild) grandson);
        }
      }
    }
  }

  public int getMaxScrollHeight() {
    return mScrollThreshold;
  }

  /**
   * @return 返回是否滑到该view的顶部
   * 1，刚好滑动到顶部
   * 2，parent已经滑到底，view的顶部依旧>scrollY
   */
  public boolean isTargetScrolledTop(@NonNull View target) {
    int top = target.getTop();
    int scrollY = getScrollY();
    return top == scrollY || top > scrollY && scrollY >= mScrollThreshold;
  }

  public void scrollToTarget(@NonNull View target) {
    scrollToTarget(target, true);
  }

  public void scrollToTarget(@NonNull View target, boolean selfScroll) {
    scrollToTarget(target, DEFAULT_DURATION, selfScroll);
  }

  /**
   * 滑动指定View到顶部
   *
   * @param target   孙子View
   * @param duration 滑动时间
   */
  public void scrollToTarget(@NonNull View target, int duration, boolean selfScroll) {
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

        int start = selfScroll ? targetIndex : targetIndex + 1;
        for (int i = start; i < parent.getChildCount(); i++) {
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
      stopAllScroll();
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

  /**
   * 增加判断滚动状态的逻辑
   */
  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    if (mVelocityTracker == null) {
      mVelocityTracker = VelocityTracker.obtain();
    }
    boolean eventAddedToVelocityTracker = false;
    final MotionEvent vtev = MotionEvent.obtain(ev);

    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        mLastTouchY = (int) (ev.getY() + 0.5f);
        break;
      case MotionEvent.ACTION_MOVE:
        final int y = (int) (ev.getY() + 0.5f);
        int dy = mLastTouchY - y;
        if (mScrollState != SCROLL_STATE_DRAGGING) {
          if (Math.abs(dy) > mTouchSlop) {
            setScrollState(SCROLL_STATE_DRAGGING);
          }
        }
        if (mScrollState == SCROLL_STATE_DRAGGING) {
          mLastTouchY = y;
        }
        break;
      case MotionEvent.ACTION_UP:
        mVelocityTracker.addMovement(vtev);
        eventAddedToVelocityTracker = true;
        mVelocityTracker.computeCurrentVelocity(1000, mMaxFlingVelocity);
        final int yvel = (int) -mVelocityTracker.getYVelocity();
        if (Math.abs(yvel) <= mMinFlingVelocity) {
          setScrollState(SCROLL_STATE_IDLE);
        }
        clearVelocityTracker();
        break;
      case MotionEvent.ACTION_CANCEL:
        setScrollState(SCROLL_STATE_IDLE);
        clearVelocityTracker();
        break;
    }
    if (!eventAddedToVelocityTracker) {
      mVelocityTracker.addMovement(vtev);
    }
    vtev.recycle();

    return super.onTouchEvent(ev);
  }

  private void clearVelocityTracker() {
    if (mVelocityTracker != null) {
      mVelocityTracker.clear();
    }
  }

  @Override
  public void onAttachedToWindow() {
    super.onAttachedToWindow();
    for (NestedLinkScrollChild child : mNestedChildren) {
      child.setOnNestedScrollListener(mNestedScrollListener);
    }
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    for (NestedLinkScrollChild child : mNestedChildren) {
      child.setOnNestedScrollListener(null);
    }
    stopScroll();
  }

  public void stopScroll() {
    setScrollState(SCROLL_STATE_IDLE);
    stopScrollerInternal();
  }

  private void stopScrollerInternal() {
    mChildTrackFlinger.stop();
    mFlinger.stop();
    mScroller.stop();
  }

  public void stopAllScroll() {
    stopScroll();
    stopChildScroll();
  }

  public void stopChildScroll() {
    for (NestedLinkScrollChild child : mNestedChildren) {
      child.stopScroll();
    }
  }

  public int getScrollState() {
    return mScrollState;
  }

  void setScrollState(int state) {
    if (state == mScrollState) {
      return;
    }
    if (BuildConfig.DEBUG) {
      Log.i(TAG, "setting scroll state to " + state + " from " + mScrollState);
    }
    mScrollState = state;
    if (state != SCROLL_STATE_SETTLING) {
      stopScrollerInternal();
    }
    dispatchOnScrollStateChanged(mScrollState);
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

  void dispatchOnNestedScrolled(@NonNull View target, int dx, int dy) {
    if (mScrollListeners != null) {
      for (int i = mScrollListeners.size() - 1; i >= 0; i--) {
        mScrollListeners.get(i).onNestedScrolled(target, dx, dy);
      }
    }
  }

  void dispatchOnNestedScrollStateChanged(@NonNull View target, int state) {
    if (mScrollListeners != null) {
      for (int i = mScrollListeners.size() - 1; i >= 0; i--) {
        mScrollListeners.get(i).onNestedScrollStateChanged(target, state);
      }
    }
  }

  private int computeChildHeight() {
    final int count = getChildCount();
    if (count == 0) {
      return 0;
    }

    View child = getChildAt(0);
    return child.getMeasuredHeight();
  }

  @SuppressLint("RestrictedApi")
  @Override
  public int computeVerticalScrollOffset() {
    int offset = 0;
    View child = getChildAt(0);
    if (child instanceof ViewGroup) {
      ViewGroup parent = (ViewGroup) child;
      for (int i = 0; i < parent.getChildCount(); i++) {
        View grandson = parent.getChildAt(i);
        if (grandson == null) {
          break;
        }
        if (grandson instanceof NestedLinkScrollChild) {
          int childOffset = ((NestedLinkScrollChild) grandson).computeVerticalScrollOffset();
          offset += childOffset;
          Log.d(TAG2, "child offset=" + childOffset + "  " + grandson.getClass().getSimpleName());
        }
      }
    }
    offset += getScrollY();
    Log.d(TAG2, "total offset=" + offset);
    return offset;
  }

  @SuppressLint("RestrictedApi")
  @Override
  public int computeVerticalScrollExtent() {
    int height = 0;
    View child = getChildAt(0);
    if (child instanceof ViewGroup) {
      ViewGroup parent = (ViewGroup) child;
      for (int i = 0; i < parent.getChildCount(); i++) {
        View grandson = parent.getChildAt(i);
        if (grandson == null) {
          break;
        }
        height += grandson.getHeight();
      }
    }
    return height;
  }

  @SuppressLint("RestrictedApi")
  @Override
  public int computeVerticalScrollRange() {
    int range = 0;
    View child = getChildAt(0);
    if (child instanceof ViewGroup) {
      ViewGroup parent = (ViewGroup) child;
      for (int i = 0; i < parent.getChildCount(); i++) {
        View grandson = parent.getChildAt(i);
        if (grandson == null) {
          break;
        }
        if (grandson instanceof NestedLinkScrollChild) {
          int childRange = ((NestedLinkScrollChild) grandson).computeVerticalScrollRange();
          range += childRange;
          Log.d(TAG2, "child range=" + childRange + " " + grandson.getClass().getSimpleName());
        } else {
          range += grandson.getHeight();
        }
      }
    }
    range += mScrollThreshold;
    Log.d(TAG2, "total range=" + range);
    return range;
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
        if (BuildConfig.DEBUG) {
          StringBuffer sb = new StringBuffer();
          sb.append("findNestedLinkChildScroll success");
          sb.append("[");
          sb.append("dyUnconsumed=" + dyUnconsumed);
          sb.append(" fling velocityY=" + curVelocity);
          sb.append("]");
          sb.append("[");
          sb.append("target=" + target.getClass().getSimpleName());
          sb.append(" child=" + childView.getClass().getSimpleName());
          sb.append("]");
          Log.d(TAG, sb.toString());
        }
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
    mScroller.stop();
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
    boolean parentScroll = getScrollY() > 0 && getScrollY() < mScrollThreshold;
    if ((mNestedChildren.indexOf(target) == 0 && getScrollY() == mScrollThreshold)
      || (mNestedChildren.indexOf(target) == 1 && getScrollY() == 0)) {
      parentScroll = true;
    }
    if (parentScroll) {
      final int oldScrollY = getScrollY();
      scrollBy(0, dy);
      final int myConsumed = getScrollY() - oldScrollY;
      consumed[1] = myConsumed;
    }

    final int[] parentConsumed = mParentScrollConsumed;
    super.onNestedPreScroll(target, dx, dy - consumed[1], parentConsumed, type);
    consumed[1] += parentConsumed[1];
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
    //解决临界处突然加速问题
    mChildTrackFlinger.mScroller.abortAnimation();
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
        setScrollState(SCROLL_STATE_IDLE);
        if (!mTrackFling) {
          stopNestedScroll(TYPE_NON_TOUCH);
        }
      }
    }

    public void trackFling(int velocityX, int velocityY) {
      mTrackFling = true;
      fling(velocityX, velocityY);
    }

    public void fling(int velocityX, int velocityY) {
      if (!mTrackFling) {
        setScrollState(SCROLL_STATE_SETTLING);
      }
      mLastY = 0;
      mType = TYPE_FLING;
      mScroller.fling(0, 0, velocityX, velocityY,
        Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
      postOnAnimation();
    }

    public void startScroll(int dx, int dy, int duration) {
      setScrollState(SCROLL_STATE_SETTLING);
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

    public void stop() {
      removeCallbacks(this);
      mScroller.abortAnimation();
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

    public void onNestedScrollStateChanged(@NonNull View target, int newState) {}

    public void onNestedScrolled(@NonNull View target, int dx, int dy) {}
  }
}
