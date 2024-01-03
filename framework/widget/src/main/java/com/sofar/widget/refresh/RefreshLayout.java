package com.sofar.widget.refresh;

import java.util.ArrayList;
import java.util.List;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import androidx.annotation.NonNull;
import androidx.core.math.MathUtils;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;

import com.sofar.widget.BuildConfig;

/**
 * NOTE: the class based on the {@link SwipeRefreshLayout} source code
 * <p>
 * The RecyclerRefreshLayout should be used whenever the user can refresh the`
 * contents of a view via a vertical swipe gesture. The activity that
 * instantiates this view should add an OnRefreshListener to be notified
 * whenever the swipe to refresh gesture is completed. The RecyclerRefreshLayout
 * will notify the listener each and every time the gesture is completed again;
 * the listener is responsible for correctly determining when to actually
 * initiate a refresh of its content. If the listener determines there should
 * not be a refresh, it must call setRefreshing(false) to cancel any visual
 * indication of a refresh. If an activity wishes to showIfNecessary just the progress
 * animation, it should call setRefreshing(true). To disable the gesture and
 * progress animation, call setEnabled(false) on the view.
 */
public abstract class RefreshLayout extends ViewGroup
  implements
  NestedScrollingParent,
  NestedScrollingChild {
  private final String TAG = "RefreshLayout";

  private static final int INVALID_INDEX = -1;
  private static final int INVALID_POINTER = -1;
  // the default height of the RefreshView
  private static final int DEFAULT_REFRESH_SIZE_DP = 56;
  private static final int DEFAULT_RESULT_SIZE_DP = 56;
  // the animation duration of the RefreshView scroll to the refresh point or the start point
  private static final int DEFAULT_ANIMATE_DURATION = 400;
  private static final int DEFAULT_RESULT_DURATION = 1500;
  // the threshold of the trigger to refresh
  private static final int DEFAULT_REFRESH_TARGET_OFFSET_DP = 56;
  private static final int DEFAULT_RESULT_TARGET_OFFSET_DP = 56;

  private static final float DECELERATE_INTERPOLATION_FACTOR = 2.0f;

  // NestedScroll
  private float mTotalUnconsumed;
  private boolean mNestedScrollInProgress;
  private final int[] mParentScrollConsumed = new int[2];
  private final int[] mParentOffsetInWindow = new int[2];
  private final NestedScrollingChildHelper mNestedScrollingChildHelper;
  private final NestedScrollingParentHelper mNestedScrollingParentHelper;
  private boolean mTouchable = false;

  // whether to remind the callback listener(OnRefreshListener)
  private boolean mIsAnimatingToStart;
  private boolean mIsRefreshing;
  private boolean mIsFitRefresh;
  private boolean mIsBeingDragged;
  private boolean mNotifyListener;
  private boolean mDispatchTargetTouchDown;

  private int mRefreshViewIndex = INVALID_INDEX;
  private int mActivePointerId = INVALID_POINTER;
  private int mAnimateToStartDuration = DEFAULT_ANIMATE_DURATION;
  private int mAnimateToRefreshDuration = DEFAULT_ANIMATE_DURATION;
  private int mAnimateToResultDuration = DEFAULT_ANIMATE_DURATION;

  private int mResultDuration = DEFAULT_RESULT_DURATION;

  private int mFrom;
  private int mTouchSlop;
  private int mRefreshViewSize;
  private int mResultViewSize;

  private float mInitialDownY;
  private float mInitialScrollY;
  private float mInitialMotionY;
  private float mLastMotionY;
  private float mCurrentTouchOffsetY;
  protected float mTargetOrRefreshViewOffsetY;
  private float mRefreshInitialOffset;
  protected float mRefreshTargetOffset;
  private float mResultTargetOffset;

  // Whether the client has set a custom refreshing position;
  private boolean mUsingCustomRefreshTargetOffset = false;
  // Whether the client has set a custom starting position;
  private boolean mUsingCustomRefreshInitialOffset = false;
  // Whether or not the RefreshView has been measured.
  private boolean mRefreshViewMeasured = false;

  private boolean isShowingResult = false;
  private boolean isResetFromResult = false;

  private RefreshStyle mRefreshStyle = RefreshStyle.NORMAL;

  private View mTarget;
  protected View mRefreshView;
  protected View mResultView;

  private DragDistanceConverter mDragDistanceConverter;

  protected RefreshStatus mRefreshStatus;
  private RefreshLayout.OnRefreshListener mOnRefreshListener;

  protected ResultStatus mResultStatus;

  private Interpolator mAnimateToStartInterpolator =
    new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
  private Interpolator mAnimateToRefreshInterpolator =
    new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
  private Interpolator mAnimateToResultInterpolator =
    new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);

  //拦截子View滚动事件
  private boolean mScrollInterceptEnable = true;

  private boolean shouldHideResult = false;

  private boolean isAnimatingHideResult = false;

  private final RefreshAnimator refreshAnimator = new RefreshAnimator();

  private void updateAnimationOffset(float offset) {
    if (!mIsAnimatingToStart) {
      return;
    }
    if (mRefreshStyle == RefreshStyle.FLOAT) {
      offset += mRefreshInitialOffset;
    }

    setTargetOrRefreshViewOffsetY((int) (offset - mTargetOrRefreshViewOffsetY));
  }

  private final Animation.AnimationListener mRefreshingListener =
    new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {
        mIsAnimatingToStart = true;
//          mRefreshStatus.refreshing();
      }

      @Override
      public void onAnimationRepeat(Animation animation) {}

      @Override
      public void onAnimationEnd(Animation animation) {
        if (mNotifyListener) {
          mNotifyListener = false;
          if (mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh();
          }
        }

        mIsAnimatingToStart = false;
      }
    };

  private final Animation.AnimationListener mResetListener = new Animation.AnimationListener() {
    @Override
    public void onAnimationStart(Animation animation) {
      mIsAnimatingToStart = true;
      mRefreshStatus.refreshComplete();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {}

    @Override
    public void onAnimationEnd(Animation animation) {
//      if (mResultView != null) {
//        mResultStatus.onHide();
//        mResultView.setVisibility(View.GONE);
//      }
      reset();
    }
  };

  private final Animation.AnimationListener mResultListener = new Animation.AnimationListener() {
    @Override
    public void onAnimationStart(Animation animation) {
      mIsAnimatingToStart = true;
      if (!isShowingResult) {
        isShowingResult = true;
        mResultStatus.onPrepare();
      }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {}

    @Override
    public void onAnimationEnd(Animation animation) {
      mRefreshStatus.reset();
      mRefreshView.setVisibility(View.GONE);
      mResultStatus.onShow();
      mIsAnimatingToStart = false;
      postDelayed(mDelayReset, mResultDuration);
    }
  };

  private final Runnable mDelayReset = new Runnable() {
    @Override
    public void run() {
      if (mIsRefreshing) {
        return;
      }
      if (!isResetFromResult) {
        isResetFromResult = true;
        animateHideResult();
      }
      if (!mIsBeingDragged && !mNestedScrollInProgress && !mIsRefreshing) {
        animateOffsetToStartPosition(mResetListener);
      }
    }
  };

  private List<RefreshOffsetChangeListener> offsetListeners = new ArrayList<>();

  public RefreshLayout(Context context) {
    this(context, null);
  }

  public RefreshLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

    final DisplayMetrics metrics = getResources().getDisplayMetrics();
    mRefreshViewSize = (int) (DEFAULT_REFRESH_SIZE_DP * metrics.density);
    mResultViewSize = (int) (DEFAULT_RESULT_SIZE_DP * metrics.density);

    mRefreshTargetOffset = DEFAULT_REFRESH_TARGET_OFFSET_DP * metrics.density;
    mResultTargetOffset = DEFAULT_RESULT_TARGET_OFFSET_DP * metrics.density;

    mTargetOrRefreshViewOffsetY = 0.0f;
    Log.i(TAG, "constructor: " + mTargetOrRefreshViewOffsetY);
    mRefreshInitialOffset = 0.0f;

    mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
    mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);

    createRefreshView();
    createDragDistanceConverter();
    setNestedScrollingEnabled(true);
    ViewCompat.setChildrenDrawingOrderEnabled(this, true);
  }

  @Override
  protected void onDetachedFromWindow() {
    reset();
    clearAnimation();
    super.onDetachedFromWindow();
  }

  private void reset() {
    setTargetOrRefreshViewToInitial();

    mCurrentTouchOffsetY = 0.0f;

    mRefreshStatus.reset();
    mRefreshView.setVisibility(View.GONE);

    mIsRefreshing = false;
    mIsAnimatingToStart = false;
    isResetFromResult = false;
    mIsFitRefresh = false;

    Log.i(TAG, "reset");
  }

  private void setTargetOrRefreshViewToInitial() {
    switch (mRefreshStyle) {
      case FLOAT:
        setTargetOrRefreshViewOffsetY((int) (mRefreshInitialOffset - mTargetOrRefreshViewOffsetY));
        break;
      default:
        setTargetOrRefreshViewOffsetY((int) (0 - mTargetOrRefreshViewOffsetY));
        break;
    }
  }

  protected abstract View onCreateRefreshView();

  public void setRefreshViewSize(int size) {
    mRefreshViewSize = size;
  }

  protected void createRefreshView() {
    mRefreshView = onCreateRefreshView();
    mRefreshView.setVisibility(View.GONE);
    if (mRefreshView instanceof RefreshStatus) {
      mRefreshStatus = (RefreshStatus) mRefreshView;
    } else {
      throw new ClassCastException("the refreshView must implement the interface IRefreshStatus");
    }

    LayoutParams layoutParams = new LayoutParams(mRefreshViewSize, mRefreshViewSize);
    addView(mRefreshView, layoutParams);
  }

  public void setResultView(@NonNull View view) {
    mResultView = view;
    mResultView.setVisibility(View.GONE);
    if (mResultView instanceof ResultStatus) {
      mResultStatus = (ResultStatus) mResultView;
    } else {
      throw new ClassCastException("the resultView must implement the interface ResultStatus");
    }
    LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, mResultViewSize);
    addView(mResultView, layoutParams);
    bringChildToFront(mRefreshView);
  }

  protected abstract DragDistanceConverter onCreateDragDistanceConvert();

  private void createDragDistanceConverter() {
    mDragDistanceConverter = onCreateDragDistanceConvert();
  }

  public void setDragDistanceConverter(@NonNull DragDistanceConverter dragDistanceConverter) {
    if (dragDistanceConverter == null) {
      throw new NullPointerException("the dragDistanceConverter can't be null");
    }
    this.mDragDistanceConverter = dragDistanceConverter;
  }

  /**
   * @param animateToStartInterpolator The interpolator used by the animation that
   *                                   move the refresh view from the refreshing point or
   *                                   (the release point) to the start point.
   */
  public void setAnimateToStartInterpolator(Interpolator animateToStartInterpolator) {
    mAnimateToStartInterpolator = animateToStartInterpolator;
  }

  /**
   * @param animateToRefreshInterpolator The interpolator used by the animation that
   *                                     move the refresh view the release point to the
   *                                     refreshing point.
   */
  public void setAnimateToRefreshInterpolator(Interpolator animateToRefreshInterpolator) {
    mAnimateToRefreshInterpolator = animateToRefreshInterpolator;
  }

  /**
   * @param animateToStartDuration The duration used by the animation that
   *                               move the refresh view from the refreshing point or
   *                               (the release point) to the start point.
   */
  public void setAnimateToStartDuration(int animateToStartDuration) {
    mAnimateToStartDuration = animateToStartDuration;
  }

  /**
   * @param animateToRefreshDuration The duration used by the animation that
   *                                 move the refresh view the release point to the refreshing
   *                                 point.
   */
  public void setAnimateToRefreshDuration(int animateToRefreshDuration) {
    mAnimateToRefreshDuration = animateToRefreshDuration;
  }

  /**
   * @param refreshTargetOffset The minimum distance that trigger refresh.
   */
  public void setRefreshTargetOffset(float refreshTargetOffset) {
    mRefreshTargetOffset = refreshTargetOffset;
    mUsingCustomRefreshTargetOffset = true;
    requestLayout();
  }

  /**
   * @param refreshInitialOffset the top position of the {@link #mRefreshView} relative to its
   *                             parent.
   */
  public void setRefreshInitialOffset(float refreshInitialOffset) {
    mRefreshInitialOffset = refreshInitialOffset;
    mUsingCustomRefreshInitialOffset = true;
    requestLayout();
  }

  public void setHandleTouchSelf(boolean handle) {
    mTouchable = handle;
  }

  @Override
  protected int getChildDrawingOrder(int childCount, int i) {
    switch (mRefreshStyle) {
      case FLOAT:
        if (mRefreshViewIndex < 0) {
          return i;
        } else if (i == childCount - 1) {
          // Draw the selected child last
          return mRefreshViewIndex;
        } else if (i >= mRefreshViewIndex) {
          // Move the children after the selected child earlier one
          return i + 1;
        } else {
          // Keep the children before the selected child the same
          return i;
        }
      default:
        if (mRefreshViewIndex < 0) {
          return i;
        } else if (i == 0) {
          // Draw the selected child first
          return mRefreshViewIndex;
        } else if (i <= mRefreshViewIndex) {
          // Move the children before the selected child earlier one
          return i - 1;
        } else {
          return i;
        }
    }
  }

  @Override
  public void requestDisallowInterceptTouchEvent(boolean b) {
    // if this is a List < L or another view that doesn't support nested
    // scrolling, ignore this request so that the vertical scroll event
    // isn't stolen
    if ((Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView)
      || (mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
      // Nope.
    } else {
      super.requestDisallowInterceptTouchEvent(b);
    }
  }

  // NestedScrollingParent

  @Override
  public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
    Log.i(TAG, "method call onStartNestedScroll child = " + child.getClass().getName() +
      " target = " + target.getClass().getName() + " nestedScrollAxes = " + nestedScrollAxes);
    mScrollInterceptEnable = true;
    switch (mRefreshStyle) {
      case FLOAT:
        return isEnabled() /*&& canChildScrollUp(mTarget) && !mIsRefreshing*/
          && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
      default:
        return isEnabled() /*&& canChildScrollUp(mTarget)*/
          && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }
  }

  @Override
  public void onNestedScrollAccepted(View child, View target, int axes) {
    // Reset the counter of how much leftover scroll needs to be consumed.
    mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
    // Dispatch up to the nested parent
    startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
    mTotalUnconsumed =
      mDragDistanceConverter.reverseConvert(mTargetOrRefreshViewOffsetY, mRefreshTargetOffset);
    mNestedScrollInProgress = true;
    mIsAnimatingToStart = false;
    shouldHideResult = true;
  }

  @Override
  public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
    // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
    // before allowing the list to scroll
    if (!mScrollInterceptEnable) {
      return;
    }
    if (isShowingResult && !isResetFromResult && shouldHideResult) {
      shouldHideResult = false;
      removeCallbacks(mDelayReset);
      animateHideResult();
    }

    if (dy > 0 && mTotalUnconsumed > 0) {
      if (dy > mTotalUnconsumed) {
        consumed[1] = dy - (int) mTotalUnconsumed;
        mTotalUnconsumed = 0;
      } else {
        mTotalUnconsumed -= dy;
        consumed[1] = dy;

      }
      Log.i(TAG, "pre scroll " + mTotalUnconsumed);
      moveSpinner(mTotalUnconsumed);
    }

    // Now let our nested parent consume the leftovers
    final int[] parentConsumed = mParentScrollConsumed;
    if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
      consumed[0] += parentConsumed[0];
      consumed[1] += parentConsumed[1];
      if (target != null && target.getParent() != null) {
        target.getParent().requestDisallowInterceptTouchEvent(true);
      }
    }
  }

  @Override
  public int getNestedScrollAxes() {
    return mNestedScrollingParentHelper.getNestedScrollAxes();
  }

  @Override
  public void onStopNestedScroll(View target) {
    mNestedScrollingParentHelper.onStopNestedScroll(target);
    mNestedScrollInProgress = false;
    // Finish the spinner for nested scrolling if we ever consumed any
    // unconsumed nested scroll
    if (mTotalUnconsumed > 0) {
      finishSpinner();
      mTotalUnconsumed = 0;
    }
    // Dispatch up our nested parent
    stopNestedScroll();
  }

  @Override
  public void onNestedScroll(final View target, final int dxConsumed, final int dyConsumed,
    final int dxUnconsumed, final int dyUnconsumed) {
    if (!mScrollInterceptEnable) {
      return;
    }
    // Dispatch up to the nested parent first
    dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
      mParentOffsetInWindow);

    // This is a bit of a hack. Nested scrolling works from the bottom up, and as we are
    // sometimes between two nested scrolling views, we need a way to be able to know when any
    // nested scrolling parent has stopped handling events. We do that by using the
    // 'offset in window 'functionality to see if we have been moved from the event.
    // This is a decent indication of whether we should take over the event stream or not.
    final int dy = dyUnconsumed + mParentOffsetInWindow[1];
    if (dy < 0) {
      mTotalUnconsumed += Math.abs(dy);
      Log.i(TAG, "nested scroll");
//      moveSpinner(mTotalUnconsumed, -dy);
      moveSpinner(mTotalUnconsumed);
    }
  }

  // NestedScrollingChild

  @Override
  public void setNestedScrollingEnabled(boolean enabled) {
    mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
  }

  @Override
  public boolean isNestedScrollingEnabled() {
    return mNestedScrollingChildHelper.isNestedScrollingEnabled();
  }

  @Override
  public boolean startNestedScroll(int axes) {
    return mNestedScrollingChildHelper.startNestedScroll(axes);
  }

  @Override
  public void stopNestedScroll() {
    mNestedScrollingChildHelper.stopNestedScroll();
  }

  @Override
  public boolean hasNestedScrollingParent() {
    return mNestedScrollingChildHelper.hasNestedScrollingParent();
  }

  @Override
  public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
    int dyUnconsumed, int[] offsetInWindow) {
    return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
      dxUnconsumed, dyUnconsumed, offsetInWindow);
  }

  @Override
  public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
    return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
  }

  @Override
  public boolean onNestedPreFling(View target, float velocityX,
    float velocityY) {
    return dispatchNestedPreFling(velocityX, velocityY);
  }

  @Override
  public boolean onNestedFling(View target, float velocityX, float velocityY,
    boolean consumed) {
    return dispatchNestedFling(velocityX, velocityY, consumed);
  }

  @Override
  public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
    return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
  }

  @Override
  public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
    return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    if (getChildCount() == 0) {
      return;
    }

    ensureTarget();
    if (mTarget == null) {
      return;
    }

    final int width = getMeasuredWidth();
    final int height = getMeasuredHeight();

    try {
      final int targetTop = reviseTargetLayoutTop(getPaddingTop());
      final int targetLeft = getPaddingLeft();
      final int targetRight = targetLeft + width - getPaddingLeft() - getPaddingRight();
      final int targetBottom = targetTop + height - getPaddingTop() - getPaddingBottom();

      mTarget.layout(targetLeft, targetTop, targetRight, targetBottom);
    } catch (Exception ignored) {
      if (BuildConfig.DEBUG) {
        throw ignored;
      }
    }

    int refreshViewLeft = (width - mRefreshView.getMeasuredWidth()) / 2;
    int refreshViewTop = reviseRefreshViewLayoutTop((int) mRefreshInitialOffset);
    int refreshViewRight = (width + mRefreshView.getMeasuredWidth()) / 2;
    int refreshViewBottom = refreshViewTop + mRefreshView.getMeasuredHeight();

    mRefreshView.layout(refreshViewLeft, refreshViewTop, refreshViewRight, refreshViewBottom);

    if (mResultView != null) {
      int resultLeft = (width - mResultView.getMeasuredWidth()) / 2;
      int resultTop = 0;
      int resultRight = (width + mResultView.getMeasuredWidth()) / 2;
      int resultBottom = mResultView.getMeasuredHeight();
      mResultView.layout(resultLeft, resultTop, resultRight, resultBottom);
    }

    Log.i(TAG,
      "onLayout: " + left + " : " + top + " : " + right + " : " + bottom + ", " + refreshViewTop +
        ", " + mRefreshInitialOffset + ", " + mTargetOrRefreshViewOffsetY);
  }

  private int reviseTargetLayoutTop(int layoutTop) {
    switch (mRefreshStyle) {
      case FLOAT:
        return layoutTop;
      case PINNED:
        return layoutTop + (int) mTargetOrRefreshViewOffsetY;
      default:
        // not consider mRefreshResistanceRate < 1.0f
        return layoutTop + (int) mTargetOrRefreshViewOffsetY;
    }
  }

  private int reviseRefreshViewLayoutTop(int layoutTop) {
    switch (mRefreshStyle) {
      case FLOAT:
        return layoutTop + (int) mTargetOrRefreshViewOffsetY;
      case PINNED:
        return layoutTop;
      default:
        // not consider mRefreshResistanceRate < 1.0f
        return layoutTop + (int) mTargetOrRefreshViewOffsetY;
    }
  }

  @Override
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    ensureTarget();
    if (mTarget == null) {
      return;
    }

    measureTarget();
    measureRefreshView(mRefreshView, widthMeasureSpec, heightMeasureSpec);
    if (mResultView != null) {
      measureRefreshView(mResultView, widthMeasureSpec, heightMeasureSpec);
    }

    if (!mRefreshViewMeasured && !mUsingCustomRefreshInitialOffset && mRefreshStyle != null) {
      switch (mRefreshStyle) {
        case PINNED:
          mTargetOrRefreshViewOffsetY = mRefreshInitialOffset = 0.0f;
          break;
        case FLOAT:
          mTargetOrRefreshViewOffsetY = mRefreshInitialOffset = -mRefreshView.getMeasuredHeight();
          break;
        default:
          mTargetOrRefreshViewOffsetY = 0.0f;
          mRefreshInitialOffset = -mRefreshView.getMeasuredHeight();
          break;
      }
    }

    if (!mRefreshViewMeasured && !mUsingCustomRefreshTargetOffset) {
      if (mRefreshTargetOffset < mRefreshView.getMeasuredHeight()) {
        mRefreshTargetOffset = mRefreshView.getMeasuredHeight();
      }
    }

    mRefreshViewMeasured = true;

    mRefreshViewIndex = -1;
    for (int index = 0; index < getChildCount(); index++) {
      if (getChildAt(index) == mRefreshView) {
        mRefreshViewIndex = index;
        break;
      }
    }
  }

  private void measureTarget() {
    mTarget.measure(MeasureSpec.makeMeasureSpec(
        getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
      MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(),
        MeasureSpec.EXACTLY));
  }

  private void measureRefreshView(View view, int widthMeasureSpec, int heightMeasureSpec) {
    final MarginLayoutParams lp = (MarginLayoutParams) view.getLayoutParams();

    final int childWidthMeasureSpec;
    if (lp.width == LayoutParams.MATCH_PARENT) {
      final int width = Math.max(0, getMeasuredWidth() - getPaddingLeft() - getPaddingRight()
        - lp.leftMargin - lp.rightMargin);
      childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
    } else {
      childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
        getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin,
        lp.width);
    }

    final int childHeightMeasureSpec;
    if (lp.height == LayoutParams.MATCH_PARENT) {
      final int height = Math.max(0, getMeasuredHeight()
        - getPaddingTop() - getPaddingBottom()
        - lp.topMargin - lp.bottomMargin);
      childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
        height, MeasureSpec.EXACTLY);
    } else {
      childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,
        getPaddingTop() + getPaddingBottom() +
          lp.topMargin + lp.bottomMargin,
        lp.height);
    }

    view.measure(childWidthMeasureSpec, childHeightMeasureSpec);
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    final int action = MotionEventCompat.getActionMasked(ev);

    switch (action) {
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        // support compile sdk version < 23
        onStopNestedScroll(this);
        break;
      default:
        break;
    }

    try {
      return super.dispatchTouchEvent(ev);
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    if (!mTouchable) {
      return false;
    }

    ensureTarget();
    if (mTarget == null) {
      return false;
    }

    if (mNestedScrollInProgress) {
      return false;
    }

    switch (mRefreshStyle) {
      case FLOAT:
        if (!isEnabled() || canChildScrollUp(mTarget)
          || mIsRefreshing || mNestedScrollInProgress) {
          // Fail fast if we're not in a state where a swipe is possible
          return false;
        }
        break;
      default:
        if ((!isEnabled() || (canChildScrollUp(mTarget) && !mDispatchTargetTouchDown))) {
          return false;
        }
        break;
    }

    final int action = MotionEventCompat.getActionMasked(ev);

    switch (action) {
      case MotionEvent.ACTION_DOWN:
        mActivePointerId = ev.getPointerId(0);
        mIsBeingDragged = mTargetOrRefreshViewOffsetY > 0;

        float initialDownY = getMotionEventY(ev, mActivePointerId);
        if (initialDownY == -1) {
          return false;
        }

        // Animation.AnimationListener.onAnimationEnd() can't be ensured to be called
        if (refreshAnimator.hasEnded()) {
          mIsAnimatingToStart = false;
        }

        mInitialDownY = initialDownY;
        mInitialScrollY = mTargetOrRefreshViewOffsetY;
        mLastMotionY = initialDownY;
        mDispatchTargetTouchDown = false;
        break;

      case MotionEvent.ACTION_MOVE:
        if (mActivePointerId == INVALID_POINTER) {
          return false;
        }

        float activeMoveY = getMotionEventY(ev, mActivePointerId);
        if (activeMoveY == -1) {
          return false;
        }

        mLastMotionY = activeMoveY;
        initDragStatus(activeMoveY);
        break;

      case MotionEvent.ACTION_POINTER_UP:
        onSecondaryPointerUp(ev);
        break;

      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        mIsBeingDragged = false;
        mActivePointerId = INVALID_POINTER;
        break;
      default:
        break;
    }

    return mIsBeingDragged;
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    if (!mTouchable) {
      return false;
    }

    ensureTarget();
    if (mTarget == null) {
      return false;
    }

    switch (mRefreshStyle) {
      case FLOAT:
        if (!isEnabled() || canChildScrollUp(mTarget) || mNestedScrollInProgress) {
          // Fail fast if we're not in a state where a swipe is possible
          return false;
        }
        break;
      default:
        if ((!isEnabled() || (canChildScrollUp(mTarget) && !mDispatchTargetTouchDown))) {
          return false;
        }
        break;
    }

    final int action = ev.getAction();

    switch (action) {
      case MotionEvent.ACTION_DOWN:
        mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
        mIsBeingDragged = mTargetOrRefreshViewOffsetY > 0;
        break;

      case MotionEvent.ACTION_MOVE: {
        if (mActivePointerId == INVALID_POINTER) {
          return false;
        }

        final float activeMoveY = getMotionEventY(ev, mActivePointerId);
        if (activeMoveY == -1) {
          return false;
        }

        float dy = activeMoveY - mInitialMotionY;
        int[] consumed = mParentScrollConsumed;
        int[] parentOffset = mParentOffsetInWindow;
        dispatchNestedPreScroll(0, -Math.round(dy), consumed, parentOffset);

        float overScrollY;
        if (mIsAnimatingToStart) {
          overScrollY = getTargetOrRefreshViewTop();

          mInitialMotionY = activeMoveY;
          mInitialScrollY = overScrollY;
          mLastMotionY = activeMoveY;
        } else {
          overScrollY = activeMoveY - mInitialMotionY - consumed[1] + mInitialScrollY;
          Log.d(TAG, "method call onTouchEvent " + " \n"
            + "=============================================" + "\n"
            + "overScrollY = " + overScrollY + "\n"
            + "activeMoveY = " + activeMoveY + "\n"
            + "mInitialMotionY = " + mInitialMotionY + "\n"
            + "consumed[1] = " + consumed[1] + "\n"
            + "mInitialScrollY = " + mInitialScrollY + "\n"
            + "============================================="
          );
        }

        if (mIsRefreshing) {
          // note: float style will not come here
          if (overScrollY <= 0) {
            if (mDispatchTargetTouchDown) {
              mTarget.dispatchTouchEvent(ev);
            } else {
              MotionEvent obtain = MotionEvent.obtain(ev);
              obtain.setAction(MotionEvent.ACTION_DOWN);
              mDispatchTargetTouchDown = true;
              mTarget.dispatchTouchEvent(obtain);
            }
          } else if (overScrollY > 0 && overScrollY < mRefreshTargetOffset) {
            if (mDispatchTargetTouchDown) {
              MotionEvent obtain = MotionEvent.obtain(ev);
              obtain.setAction(MotionEvent.ACTION_CANCEL);
              mDispatchTargetTouchDown = false;
              mTarget.dispatchTouchEvent(obtain);
            }
          }
          Log.i(TAG, "moveSpinner refreshing -- " + mInitialScrollY + " -- "
            + (activeMoveY - mInitialMotionY));
          moveSpinner(overScrollY);
        } else {
          if (mIsBeingDragged) {
            if (overScrollY > 0) {
              moveSpinner(overScrollY);
              Log.i(TAG, "onTouchEvent moveSpinner not refreshing -- " + overScrollY + " -- "
                + (activeMoveY - mInitialMotionY));
            } else {
              Log.i(TAG, "is Being Dragged, but over scroll Y < 0");
              return false;
            }
          } else {
            Log.i(TAG, "is not Being Dragged, init drag status");
            initDragStatus(activeMoveY);
          }
        }
        break;
      }

      case MotionEventCompat.ACTION_POINTER_DOWN: {
        onNewerPointerDown(ev);
        break;
      }

      case MotionEvent.ACTION_POINTER_UP:
        onSecondaryPointerUp(ev);
        break;

      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL: {
        if (mActivePointerId == INVALID_POINTER
          || getMotionEventY(ev, mActivePointerId) == -1) {
          resetTouchEvent();
          return false;
        }

        if (mIsRefreshing || mIsAnimatingToStart) {
          if (mDispatchTargetTouchDown) {
            mTarget.dispatchTouchEvent(ev);
          }
          resetTouchEvent();
          return false;
        }

        resetTouchEvent();
        finishSpinner();
        return false;
      }
      default:
        break;
    }

    return true;
  }

  private void resetTouchEvent() {
    mInitialScrollY = 0.0f;

    mIsBeingDragged = false;
    mDispatchTargetTouchDown = false;
    mActivePointerId = INVALID_POINTER;
  }

  /**
   * Notify the widget that refresh state has changed. Do not call this when
   * refresh is triggered by a swipe gesture.
   *
   * @param refreshing Whether or not the view should showIfNecessary refresh progress.
   */
  public void setRefreshing(boolean refreshing) {
    if (mIsRefreshing == refreshing) {
      return;
    }
    if (refreshing) {
      if (getAnimation() != null && !getAnimation().hasEnded()) {
        getAnimation().setAnimationListener(null);
        clearAnimation();
        reset();
      }
      mIsRefreshing = refreshing;
      mNotifyListener = false;

      animateToRefreshingPosition(mRefreshingListener, true);
    } else {
      setRefreshing(refreshing, false);
    }
  }

  private void setRefreshing(boolean refreshing, final boolean notify) {
    if (mIsRefreshing != refreshing) {
      mNotifyListener = notify;
      mIsRefreshing = refreshing;
      if (refreshing) {
        animateToRefreshingPosition(mRefreshingListener, false);
        if (isShowingResult) {
          animateHideResult();
        }
      } else {
        if (!notify && mResultView != null && mResultStatus.isAvailable()) {
          mResultView.animate().cancel();
          mResultView.setTranslationY(0);
          isAnimatingHideResult = false;
          animateToResultPosition(mResultListener,
            mTargetOrRefreshViewOffsetY >= mResultTargetOffset);
          return;
        }
        isResetFromResult = false;
        animateOffsetToStartPosition(mResetListener);
      }
    }
  }

  private void initDragStatus(float activeMoveY) {
    float diff = activeMoveY - mInitialDownY;
    if (mIsRefreshing && (diff > mTouchSlop || mTargetOrRefreshViewOffsetY > 0)) {
      mIsBeingDragged = true;
      mInitialMotionY = activeMoveY;
      mLastMotionY = mInitialMotionY;
      // scroll direction: from up to down
    } else if (!mIsBeingDragged && diff > mTouchSlop) {
      mInitialMotionY = activeMoveY;
      mLastMotionY = mInitialMotionY;
      mIsBeingDragged = true;
    }
  }

  private void animateToPosition(float target, Object tag, boolean force,
    Animation.AnimationListener listener) {
    refreshAnimator.setAnimationListener(null);
    clearAnimation();
    mScrollInterceptEnable = false;
    mIsAnimatingToStart = false;
    refreshAnimator.reset();
    refreshAnimator.tag = tag;
    refreshAnimator.setPosition(mTargetOrRefreshViewOffsetY, target, force);
    if (refreshAnimator.getDuration() <= 0) {
      if (listener != null) {
        listener.onAnimationStart(null);
        listener.onAnimationEnd(null);
      }
      return;
    }

    if (listener != null) {
      refreshAnimator.setAnimationListener(listener);
    }

    startAnimation(refreshAnimator);
    mIsAnimatingToStart = true;
  }

  private void animateOffsetToStartPosition(Animation.AnimationListener listener) {
    animateToPosition(0, "start", false, listener);
  }

  private void animateToRefreshingPosition(Animation.AnimationListener listener, boolean force) {
    if (!"refresh".equals(refreshAnimator.tag)) {
      mRefreshStatus.refreshing();
    }
    animateToPosition(mRefreshTargetOffset, "refresh", force, listener);
  }

  private void animateToResultPosition(Animation.AnimationListener listener, boolean animate) {
    mResultView.setVisibility(View.VISIBLE);
    animateToPosition(animate ? mResultTargetOffset : mTargetOrRefreshViewOffsetY, "result",
      false, listener);
  }

  /**
   * @param targetOrRefreshViewOffsetY the top position of the target
   *                                   or the RefreshView relative to its parent.
   */
  private void moveSpinner(float targetOrRefreshViewOffsetY) {
    mCurrentTouchOffsetY = targetOrRefreshViewOffsetY;
    Log.d(TAG, " method call moveSpinner before " + " targetOrRefreshViewOffsetY = " +
      targetOrRefreshViewOffsetY + ", " + mTotalUnconsumed);

    float convertScrollOffset =
      mDragDistanceConverter.convert(targetOrRefreshViewOffsetY, mRefreshTargetOffset);
    if (mRefreshStyle == RefreshStyle.FLOAT) {
      convertScrollOffset += mRefreshInitialOffset;
    }

    if (!mIsRefreshing) {
      if (convertScrollOffset > mRefreshInitialOffset && !mIsFitRefresh) {
        mIsFitRefresh = true;
        mRefreshStatus.pullToRefresh();
      } else if (convertScrollOffset <= mRefreshInitialOffset && mIsFitRefresh) {
        mIsFitRefresh = false;
        mRefreshStatus.releaseToRefresh();
      }
    }

    Log.d(TAG, "method call moveSpinner after " + " \n"
      + "=============================================" + "\n"
      + "targetOrRefreshViewOffsetY = " + targetOrRefreshViewOffsetY + "\n"
      + "refreshTargetOffset = " + mRefreshInitialOffset + "\n"
      + "convertScrollOffset = " + convertScrollOffset + "\n"
      + "mTargetOrRefreshViewOffsetY = " + mTargetOrRefreshViewOffsetY + "\n"
      + "mRefreshTargetOffset = " + mRefreshTargetOffset + "\n"
      + "mIsFitRefresh = " + mIsFitRefresh + "\n"
      + "============================================="
    );

    setTargetOrRefreshViewOffsetY((int) (convertScrollOffset - mTargetOrRefreshViewOffsetY));
  }

  private void finishSpinner() {
    if (mIsRefreshing) {
      if (mTargetOrRefreshViewOffsetY > mRefreshTargetOffset) {
        animateToRefreshingPosition(mRefreshingListener, false);
      }
    } else {
      if (!mIsAnimatingToStart && shouldRefresh()) {
        setRefreshing(true, true);
      } else if (isShowingResult && !isResetFromResult && !isAnimatingHideResult) {
        animateToResultPosition(mResultListener,
          mTargetOrRefreshViewOffsetY >= mResultTargetOffset);
      } else {
        mIsRefreshing = false;
        isResetFromResult = false;
        animateOffsetToStartPosition(mResetListener);
      }
    }
  }

  protected boolean shouldRefresh() {
    return getTargetOrRefreshViewOffset() > mRefreshTargetOffset;
  }

  private void onNewerPointerDown(MotionEvent ev) {
    final int index = MotionEventCompat.getActionIndex(ev);
    mActivePointerId = MotionEventCompat.getPointerId(ev, index);

    mInitialMotionY = getMotionEventY(ev, mActivePointerId) - mCurrentTouchOffsetY;

    Log.i(TAG, " onDown " + mInitialMotionY);
  }

  private void onSecondaryPointerUp(MotionEvent ev) {
    int pointerIndex = MotionEventCompat.getActionIndex(ev);
    int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);

    if (pointerId == mActivePointerId) {
      final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
      mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
    }

    mInitialMotionY = getMotionEventY(ev, mActivePointerId) - mCurrentTouchOffsetY;

    Log.i(TAG, " onUp " + mInitialMotionY);
  }

  private void setTargetOrRefreshViewOffsetY(int offsetY) {
    if (mTarget == null) {
      return;
    }
    if (mTargetOrRefreshViewOffsetY + offsetY < 0) {
      offsetY = (int) -mTargetOrRefreshViewOffsetY;
    }
    switch (mRefreshStyle) {
      case FLOAT:
        mRefreshView.offsetTopAndBottom(offsetY);
        mTargetOrRefreshViewOffsetY = mRefreshView.getTop();
        break;
      case PINNED:
        mTarget.offsetTopAndBottom(offsetY);
        mTargetOrRefreshViewOffsetY = mTarget.getTop();
        break;
      default:
        mTarget.offsetTopAndBottom(offsetY);
        mRefreshView.offsetTopAndBottom(offsetY);
        mTargetOrRefreshViewOffsetY = mTarget.getTop();
        break;
    }

    switch (mRefreshStyle) {
      case FLOAT:
        mRefreshStatus.pullProgress(mTargetOrRefreshViewOffsetY,
          (mTargetOrRefreshViewOffsetY - mRefreshInitialOffset) / mRefreshTargetOffset);
        break;
      default:
        mRefreshStatus.pullProgress(mTargetOrRefreshViewOffsetY,
          mTargetOrRefreshViewOffsetY / mRefreshTargetOffset);
        break;
    }

    if (mRefreshStyle != RefreshStyle.FLOAT) {
      onOffsetChanged(mTargetOrRefreshViewOffsetY);
    }

    if (mRefreshView.getVisibility() != View.VISIBLE
//        && (!isShowingResult || isResetFromResult)
      && (mResultView == null || mTargetOrRefreshViewOffsetY >
      (isShowingResult ? mResultView.getHeight() : mTargetOrRefreshViewOffsetY / 2f))) {
      mRefreshView.setVisibility(View.VISIBLE);
    } else if (mRefreshView.getVisibility() == View.VISIBLE && isShowingResult
      && mResultView != null && mTargetOrRefreshViewOffsetY < mRefreshView.getHeight() / 2.5f) {
      mRefreshView.setVisibility(View.GONE);
    }

    invalidate();
  }

  private int getTargetOrRefreshViewTop() {
    switch (mRefreshStyle) {
      case FLOAT:
        return mRefreshView.getTop();
      default:
        return mTarget.getTop();
    }
  }

  private int getTargetOrRefreshViewOffset() {
    switch (mRefreshStyle) {
      case FLOAT:
        return (int) (mRefreshView.getTop() - mRefreshInitialOffset);
      default:
        return mTarget.getTop();
    }
  }

  private float getMotionEventY(MotionEvent ev, int activePointerId) {
    final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
    if (index < 0) {
      return -1;
    }
    return MotionEventCompat.getY(ev, index);
  }

  private boolean canChildScrollUp(View mTarget) {
    if (mTarget == null) {
      return false;
    }

    if (Build.VERSION.SDK_INT < 14 && mTarget instanceof AbsListView) {
      final AbsListView absListView = (AbsListView) mTarget;
      return absListView.getChildCount() > 0
        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
        .getTop() < absListView.getPaddingTop());
    }

    if (mTarget instanceof ViewGroup) {
      int childCount = ((ViewGroup) mTarget).getChildCount();
      for (int i = 0; i < childCount; i++) {
        View child = ((ViewGroup) mTarget).getChildAt(i);
        if (canChildScrollUp(child)) {
          return true;
        }
      }
    }

    return ViewCompat.canScrollVertically(mTarget, -1);
  }

  private void ensureTarget() {
    if (!isTargetValid()) {
      for (int i = 0; i < getChildCount(); i++) {
        View child = getChildAt(i);
        if (!child.equals(mRefreshView) && !child.equals(mResultView)) {
          mTarget = child;
          break;
        }
      }
    }
  }

  private boolean isTargetValid() {
    for (int i = 0; i < getChildCount(); i++) {
      if (mTarget == getChildAt(i)) {
        return true;
      }
    }

    return false;
  }

  public boolean isRefreshing() {
    return mIsRefreshing;
  }

  /**
   * Set the style of the RefreshView.
   *
   * @param refreshStyle One of {@link RefreshStyle#NORMAL}
   *                     , {@link RefreshStyle#PINNED}, or {@link RefreshStyle#FLOAT}
   */
  public void setRefreshStyle(@NonNull RefreshStyle refreshStyle) {
    mRefreshStyle = refreshStyle;
  }

  public enum RefreshStyle {
    NORMAL,
    PINNED,
    FLOAT
  }

  /**
   * Set the listener to be notified when a refresh is triggered via the swipe
   * gesture.
   */
  public void setOnRefreshListener(RefreshLayout.OnRefreshListener listener) {
    mOnRefreshListener = listener;
  }

  public void addOnRefreshOffsetChangeListener(RefreshOffsetChangeListener listener) {
    offsetListeners.remove(listener);
    offsetListeners.add(listener);
  }

  public void removeOnRefreshOffsetChangeListener(RefreshOffsetChangeListener listener) {
    offsetListeners.remove(listener);
  }

  private void onOffsetChanged(float offset) {
    for (RefreshOffsetChangeListener listener : offsetListeners) {
      listener.onOffsetChanged(offset);
    }
  }

  public View getRefreshView() {
    return mRefreshView;
  }

  public void showSilentResultIfNecessary() {
    if (!mIsRefreshing && !isResetFromResult
      && (getAnimation() == null || getAnimation().hasEnded())
      && mTargetOrRefreshViewOffsetY <= 0
      && mResultStatus != null && mResultStatus.isAvailable()) {
      isShowingResult = true;
      mResultView.setVisibility(View.VISIBLE);
      mResultStatus.onPrepare();
      mResultStatus.onShow();
      postDelayed(mDelayReset, mResultDuration + 300);
    }
  }

  private void animateHideResult() {
    if (isAnimatingHideResult) {
      return;
    }
    isAnimatingHideResult = true;
    mResultView.animate().translationY(-mResultView.getHeight())
      .setDuration(mResultView.getHeight() != 0
        ? (long) (
        (1 - MathUtils.clamp(mResultView.getTranslationY() / mResultView.getHeight(), 0f, 1f)) *
          350)
        : 0)
      .setInterpolator(mAnimateToStartInterpolator)
      .setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
          if (mResultView.getVisibility() == View.VISIBLE) {
            setTargetOrRefreshViewOffsetY(0);
          }
        }
      })
      .withEndAction(() -> {
        isAnimatingHideResult = false;
        isShowingResult = false;
        isResetFromResult = false;
        mResultView.setVisibility(View.GONE);
        mResultView.setTranslationY(0);
        mResultStatus.onHide();
        setTargetOrRefreshViewOffsetY(0);
      })
      .start();
  }

  /**
   * Per-child layout information for layouts that support margins.
   */
  public static class LayoutParams extends MarginLayoutParams {

    public LayoutParams(Context c, AttributeSet attrs) {
      super(c, attrs);
    }

    public LayoutParams(int width, int height) {
      super(width, height);
    }

    public LayoutParams(MarginLayoutParams source) {
      super(source);
    }

    public LayoutParams(ViewGroup.LayoutParams source) {
      super(source);
    }
  }

  @Override
  public LayoutParams generateLayoutParams(AttributeSet attrs) {
    return new LayoutParams(getContext(), attrs);
  }

  @Override
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
    return new LayoutParams(p);
  }

  @Override
  protected LayoutParams generateDefaultLayoutParams() {
    return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
  }

  @Override
  protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
    return p instanceof LayoutParams;
  }

  class RefreshAnimator extends Animation {

    private float from, to;

    private Object tag;

    public RefreshAnimator() {
      setInterpolator(new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR));
    }

    public void setTag(Object tag) {
      this.tag = tag;
    }

    public void setPosition(float from, float to, boolean force) {
      this.from = from;
      this.to = to;
      setDuration(from <= 0 && !force ? 0 : computeDuration());
    }

    private long computeDuration() {
      return (long) (Math.min(1.0f, Math.abs(to - from) / mRefreshTargetOffset) *
        mAnimateToRefreshDuration);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
      updateAnimationOffset(from + (to - from) * interpolatedTime);
    }
  }

  public interface OnRefreshListener {
    void onRefresh();
  }
}

