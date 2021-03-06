package com.sofar.widget.recycler.layoutmanager.stack;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


public class StackLayoutManager extends RecyclerView.LayoutManager {

  int mOrientation;
  int mVisibleItemCount;
  int mScrollOffset;

  //做动画的组件，支持自定义
  @NonNull
  private StackAnimation mAnimation;
  //做布局的组件，支持自定义
  @NonNull
  private StackLayout mLayout;

  //是否是翻页效果
  private boolean mPagerMode = true;

  //触发翻页效果的最低 Fling速度
  private int mPagerFlingVelocity = 0;

  //标志当前滚动是否是调用scrollToCenter之后触发的滚动
  private boolean mFixScrolling = false;

  //fling的方向，用来判断是前翻还是后翻
  private int mFlingOrientation = FlingOrientation.NONE;

  //当前所处item对应的位置
  private int itemPosition = 0;

  //判断item位置是否发生了改变
  private boolean isItemPositionChanged = false;

  //item 位置发生改变的回调
  @Nullable
  private ItemChangedListener itemChangedListener;

  private RecyclerView.OnFlingListener mOnFlingListener;
  private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
      if (newState == RecyclerView.SCROLL_STATE_IDLE) {
        if (!mFixScrolling) {
          mFixScrolling = true;
          calculateAndScrollToTarget(recyclerView);
        } else {
          //表示此次 IDLE 是由修正位置结束触发的
          mFixScrolling = false;
        }
      } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
        mFixScrolling = false;
      }
    }
  };

  public StackLayoutManager() {
    this(Orientation.RIGHT_TO_LEFT, 3);
  }

  public StackLayoutManager(int orientation, int visibleCount) {
    this.mOrientation = orientation;
    this.mVisibleItemCount = visibleCount;
    init();
  }

  private void init() {
    mAnimation = new DefaultAnimation(mOrientation, mVisibleItemCount);
    mLayout = new DefaultLayout(mOrientation, mVisibleItemCount, 30);

    switch (mOrientation) {
      case Orientation.RIGHT_TO_LEFT:
      case Orientation.BOTTOM_TO_TOP:
        mScrollOffset = 0;
        break;
      default:
        mScrollOffset = Integer.MAX_VALUE;
        break;
    }
  }

  @Override
  public RecyclerView.LayoutParams generateDefaultLayoutParams() {
    return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
      ViewGroup.LayoutParams.WRAP_CONTENT);
  }

  @Override
  public void onAttachedToWindow(RecyclerView view) {
    super.onAttachedToWindow(view);
    mOnFlingListener = new StackOnFlingListener(view);
    view.setOnFlingListener(mOnFlingListener);
    view.addOnScrollListener(mOnScrollListener);
  }

  @Override
  public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
    super.onDetachedFromWindow(view, recycler);
    view.setOnFlingListener(null);
    view.removeOnScrollListener(mOnScrollListener);
  }

  @Override
  public boolean canScrollHorizontally() {
    if (getItemCount() == 0) {
      return false;
    }
    return mOrientation == Orientation.LEFT_TO_RIGHT || mOrientation == Orientation.RIGHT_TO_LEFT;
  }

  @Override
  public boolean canScrollVertically() {
    if (getItemCount() == 0) {
      return false;
    }
    return mOrientation == Orientation.TOP_TO_BOTTOM || mOrientation == Orientation.BOTTOM_TO_TOP;
  }

  @Override
  public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
    mLayout.requestLayout();
    removeAndRecycleAllViews(recycler);

    if (getItemCount() > 0) {
      mScrollOffset = getValidOffset(mScrollOffset);
      loadItemView(recycler);
    }
  }

  @Override
  public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler,
    RecyclerView.State state) {
    return handleScrollBy(dx, recycler);
  }

  @Override
  public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
    return handleScrollBy(dy, recycler);
  }

  @Override
  public void scrollToPosition(int position) {
    if (position < 0 || position >= getItemCount()) {
      throw new ArrayIndexOutOfBoundsException("$position is out of bound [0..$itemCount-1]");
    }
    mScrollOffset = getPositionOffset(position);
    requestLayout();
  }

  @Override
  public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
    int position) {
    if (position < 0 || position >= getItemCount()) {
      throw new ArrayIndexOutOfBoundsException("$position is out of bound [0..$itemCount-1]");
    }
    mFixScrolling = true;
    scrollToCenter(position, recyclerView, true);
  }

  private int getValidOffset(int expectOffset) {
    switch (mOrientation) {
      case Orientation.RIGHT_TO_LEFT:
      case Orientation.LEFT_TO_RIGHT:
        return Math.max(Math.min(getWidth() * (getItemCount() - 1), expectOffset), 0);
      default:
        return Math.max(Math.min(getHeight() * (getItemCount() - 1), expectOffset), 0);
    }
  }

  private int handleScrollBy(int offset, RecyclerView.Recycler recycler) {
    //期望值，不得超过最大最小值，所以期望值不一定等于实际值
    int expectOffset = mScrollOffset + offset;

    //实际值
    mScrollOffset = getValidOffset(expectOffset);

    //实际偏移，超过最大最小值之后的偏移都应该是0，该值作为返回值，否则在极限位置进行滚动的时候不会出现弹性阴影
    int exactMove = mScrollOffset - expectOffset + offset;

    if (exactMove == 0) {
      //itemViews 位置都不会改变，直接 return
      return 0;
    }

    detachAndScrapAttachedViews(recycler);

    loadItemView(recycler);
    return exactMove;
  }

  private void loadItemView(RecyclerView.Recycler recycler) {
    int firstVisiblePosition = getFirstVisibleItemPosition();
    int lastVisiblePosition = getLastVisibleItemPosition();

    //位移百分比
    float movePercent = getFirstVisibleItemMovePercent();
    for (int i = lastVisiblePosition; i >= firstVisiblePosition; i--) {
      View view = recycler.getViewForPosition(i);
      //添加到recycleView 中
      addView(view);
      //测量
      measureChild(view, 0, 0);
      //布局
      mLayout.doLayout(this, mScrollOffset, movePercent, view, i - firstVisiblePosition);
      //做动画
      mAnimation.doAnimation(movePercent, view, i - firstVisiblePosition);
    }

    //尝试更新当前item的位置并通知外界
    updatePositionRecordAndNotify(firstVisiblePosition);

    //重用
    if (firstVisiblePosition - 1 >= 0) {
      View view = recycler.getViewForPosition(firstVisiblePosition - 1);
      resetViewAnimateProperty(view);
      removeAndRecycleView(view, recycler);
    }
    if (lastVisiblePosition + 1 < getItemCount()) {
      View view = recycler.getViewForPosition(lastVisiblePosition + 1);
      resetViewAnimateProperty(view);
      removeAndRecycleView(view, recycler);
    }
  }

  private int getLastVisibleItemPosition() {
    int firstVisiblePosition = getFirstVisibleItemPosition();
    if (firstVisiblePosition + mVisibleItemCount > getItemCount() - 1) {
      return getItemCount() - 1;
    } else {
      return firstVisiblePosition + mVisibleItemCount;
    }
  }

  private void calculateAndScrollToTarget(RecyclerView view) {
    int targetPosition = calculateCenterPosition(getFirstVisibleItemPosition());
    scrollToCenter(targetPosition, view, true);
  }

  private int calculateCenterPosition(int position) {
    //当是 Fling 触发的时候
    int triggerOrientation = mFlingOrientation;
    mFlingOrientation = FlingOrientation.NONE;
    switch (mOrientation) {
      case Orientation.RIGHT_TO_LEFT:
        if (triggerOrientation == FlingOrientation.RIGHT_TO_LEFT) {
          return position + 1;
        } else if (triggerOrientation == FlingOrientation.LEFT_TO_RIGHT) {
          return position;
        }
      case Orientation.LEFT_TO_RIGHT:
        if (triggerOrientation == FlingOrientation.LEFT_TO_RIGHT) {
          return position + 1;
        } else if (triggerOrientation == FlingOrientation.RIGHT_TO_LEFT) {
          return position;
        }
      case Orientation.BOTTOM_TO_TOP:
        if (triggerOrientation == FlingOrientation.BOTTOM_TO_TOP) {
          return position + 1;
        } else if (triggerOrientation == FlingOrientation.TOP_TO_BOTTOM) {
          return position;
        }
      case Orientation.TOP_TO_BOTTOM:
        if (triggerOrientation == FlingOrientation.TOP_TO_BOTTOM) {
          return position + 1;
        } else if (triggerOrientation == FlingOrientation.BOTTOM_TO_TOP) {
          return position;
        }
    }
    //当不是 fling 触发的时候
    float percent = getFirstVisibleItemMovePercent();
    //向左移动超过50% position(firstVisibleItemPosition)++， 否则 position不变
    if (percent < 0.5) {
      return position;
    } else {
      return position + 1;
    }
  }

  private void scrollToCenter(int targetPosition, RecyclerView recyclerView, boolean animation) {
    int targetOffset = getPositionOffset(targetPosition);
    switch (mOrientation) {
      case Orientation.RIGHT_TO_LEFT:
      case Orientation.LEFT_TO_RIGHT:
        if (animation) {
          recyclerView.smoothScrollBy(targetOffset - mScrollOffset, 0);
        } else {
          recyclerView.scrollBy(targetOffset - mScrollOffset, 0);
        }
        break;
      default:
        if (animation) {
          recyclerView.smoothScrollBy(0, targetOffset - mScrollOffset);
        } else {
          recyclerView.scrollBy(0, targetOffset - mScrollOffset);
        }
        break;
    }
  }

  private int getPositionOffset(int position) {
    int offset = 0;
    switch (mOrientation) {
      case Orientation.RIGHT_TO_LEFT:
        offset = position * getWidth();
        break;
      case Orientation.LEFT_TO_RIGHT:
        offset = (getItemCount() - 1 - position) * getWidth();
        break;
      case Orientation.BOTTOM_TO_TOP:
        offset = position * getHeight();
        break;
      case Orientation.TOP_TO_BOTTOM:
        offset = (getItemCount() - 1 - position) * getHeight();
        break;
    }
    return offset;
  }

  private float getFirstVisibleItemMovePercent() {
    if (getWidth() == 0 || getHeight() == 0) {
      return 0f;
    }
    switch (mOrientation) {
      case Orientation.RIGHT_TO_LEFT:
        return (mScrollOffset % getWidth()) * 1.0f / getWidth();
      case Orientation.LEFT_TO_RIGHT: {
        float targetPercent = 1 - (mScrollOffset % getWidth()) * 1.0f / getWidth();
        if (targetPercent == 1f) {
          return 0f;
        } else {
          return targetPercent;
        }
      }
      case Orientation.BOTTOM_TO_TOP:
        return (mScrollOffset % getHeight()) * 1.0f / getHeight();
      case Orientation.TOP_TO_BOTTOM: {
        float targetPercent = 1 - (mScrollOffset % getHeight()) * 1.0f / getHeight();
        if (targetPercent == 1f) {
          return 0f;
        } else {
          return targetPercent;
        }
      }
    }
    return 0;
  }

  private void updatePositionRecordAndNotify(int position) {
    if (itemChangedListener == null) {
      return;
    }
    if (position != itemPosition) {
      isItemPositionChanged = true;
      itemPosition = position;
      itemChangedListener.onItemChanged(itemPosition);
    } else {
      isItemPositionChanged = false;
    }
  }

  private void resetViewAnimateProperty(View view) {
    view.setRotationY(0);
    view.setRotationX(0);
    view.setScaleX(1);
    view.setScaleY(1);
    view.setAlpha(1);
  }

  /**
   * 设置是否为ViewPager 式翻页模式.
   * 当设置为 true 的时候，可以配合[StackLayoutManager.setPagerFlingVelocity]设置触发翻页的最小速度.
   *
   * @param isPagerMode 这个值默认是 false，当设置为 true 的时候，会有 viewPager 翻页效果.
   */
  public void setPagerMode(boolean isPagerMode) {
    mPagerMode = isPagerMode;
  }

  /**
   * @return 当前是否为ViewPager翻页模式.
   */
  public boolean getPagerMode() {
    return mPagerMode;
  }

  /**
   * 设置触发ViewPager翻页效果的最小速度.
   * <p>
   * 该值仅在 [StackLayoutManager.getPagerMode] == true的时候有效.
   *
   * @param velocity 默认值是2000.
   */
  public void setPagerFlingVelocity(@IntRange(from = 0, to = Integer.MAX_VALUE) int velocity) {
    mPagerFlingVelocity = Math.min(Integer.MAX_VALUE, Math.max(0, velocity));
  }

  /**
   * @return 当前触发翻页的最小 fling 速度.
   */
  public int getPagerFlingVelocity() {
    return mPagerFlingVelocity;
  }

  /**
   * 设置recyclerView 静止时候可见的itemView 个数.
   *
   * @param count 可见 itemView，默认为3
   */
  public void setVisibleItemCount(@IntRange(from = 1, to = Integer.MAX_VALUE) int count) {
    mVisibleItemCount = Math.min(getItemCount() - 1, Math.max(1, count));
    mAnimation.setVisibleCount(mVisibleItemCount);
  }

  /**
   * 获取recyclerView 静止时候可见的itemView 个数.
   *
   * @return 静止时候可见的itemView 个数，默认为3.
   */
  public int getVisibleItemCount() {
    return mVisibleItemCount;
  }

  public void setLayout(@NonNull StackLayout layout) {
    mLayout = layout;
  }

  /**
   * 设置 item 偏移值，即第 i 个 item 相对于 第 i-1个 item 在水平方向的偏移值，默认是40px.
   *
   * @param offset 每个 item 相对于前一个的偏移值.
   */
  public void setItemOffset(int offset) {
    mLayout.setItemOffset(offset);
  }

  /**
   * 获取每个 item 相对于前一个的水平偏移值.
   *
   * @return 每个 item 相对于前一个的水平偏移值.
   */
  public int getItemOffset() {
    return mLayout.getItemOffset();
  }

  /**
   * 设置item 移动动画.
   *
   * @param animation item 移动动画.
   */
  public void setAnimation(@NonNull StackAnimation animation) {
    mAnimation = animation;
  }

  /**
   * 获取 item 移动动画.
   *
   * @return item 移动动画.
   */
  @NonNull
  public StackAnimation getAnimation() {
    return mAnimation;
  }

  /**
   * 获取StackLayoutManager 的滚动方向.
   *
   * @return StackLayoutManager 的滚动方向.
   */
  public int getScrollOrientation() {
    return mOrientation;
  }

  /**
   * 返回第一个可见 itemView 的位置.
   *
   * @return 返回第一个可见 itemView 的位置.
   */
  public int getFirstVisibleItemPosition() {
    if (getWidth() == 0 || getHeight() == 0) {
      return 0;
    }

    switch (mOrientation) {
      case StackLayoutManager.Orientation.RIGHT_TO_LEFT:
        return (int) Math.floor((mScrollOffset * 1.0 / getWidth()));
      case StackLayoutManager.Orientation.LEFT_TO_RIGHT:
        return (int) (getItemCount() - 1 - Math.ceil((mScrollOffset * 1.0 / getWidth())));
      case StackLayoutManager.Orientation.BOTTOM_TO_TOP:
        return (int) Math.floor((mScrollOffset * 1.0 / getHeight()));
      case StackLayoutManager.Orientation.TOP_TO_BOTTOM:
        return (int) (getItemCount() - 1 - Math.ceil((mScrollOffset * 1.0 / getHeight())));
    }
    return 0;
  }

  /**
   * 设置 item 位置改变时触发的回调
   */
  public void setItemChangedListener(ItemChangedListener listener) {
    itemChangedListener = listener;
  }

  public interface ItemChangedListener {
    void onItemChanged(int position);
  }

  private class StackOnFlingListener extends RecyclerView.OnFlingListener {

    RecyclerView view;

    public StackOnFlingListener(RecyclerView view) {
      this.view = view;
    }

    @Override
    public boolean onFling(int velocityX, int velocityY) {
      if (mPagerMode) {
        if (mOrientation == Orientation.RIGHT_TO_LEFT ||
          mOrientation == Orientation.LEFT_TO_RIGHT) {
          if (velocityX > mPagerFlingVelocity) {
            mFlingOrientation = FlingOrientation.RIGHT_TO_LEFT;
          } else if (velocityX < -mPagerFlingVelocity) {
            mFlingOrientation = FlingOrientation.LEFT_TO_RIGHT;
          } else {
            mFlingOrientation = FlingOrientation.NONE;
          }
        } else {
          if (velocityY > mPagerFlingVelocity) {
            mFlingOrientation = FlingOrientation.BOTTOM_TO_TOP;
          } else if (velocityY < -mPagerFlingVelocity) {
            mFlingOrientation = FlingOrientation.TOP_TO_BOTTOM;
          } else {
            mFlingOrientation = FlingOrientation.NONE;
          }
        }
        if (mScrollOffset >= 1 && mScrollOffset < getWidth() * (getItemCount() - 1)) { //边界不需要滚动
          mFixScrolling = true;
        }
        calculateAndScrollToTarget(view);
      }
      return mPagerMode;
    }
  }

  public @interface Orientation {
    int LEFT_TO_RIGHT = 0;
    int RIGHT_TO_LEFT = 1;
    int TOP_TO_BOTTOM = 2;
    int BOTTOM_TO_TOP = 3;
  }

  public @interface FlingOrientation {
    int NONE = -1;
    int LEFT_TO_RIGHT = 0;
    int RIGHT_TO_LEFT = 1;
    int TOP_TO_BOTTOM = 2;
    int BOTTOM_TO_TOP = 3;
  }
}
