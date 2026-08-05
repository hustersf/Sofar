package com.sofar.widget.refresh;

/**
 * {@link RefreshLayout} 类中所有的下拉刷新组件都需要继承的接口
 */
public interface RefreshStatus {
  /**
   * When the content view has reached top and refresh has been completed, view will be reset.
   */
  void reset();

  /**
   * Refresh View is refreshing
   */
  void refreshing();

  /**
   * refresh has been completed
   */
  void refreshComplete();

  /**
   * Refresh View is dropped down to the refresh point
   */
  void pullToRefresh();

  /**
   * Refresh View is released into the refresh point
   */
  void releaseToRefresh();

  /**
   * @param pullDistance The drop-down distance of the refresh View
   * @param pullProgress The drop-down progress of the refresh View and the pullProgress may be more
   *          than 1.0f
   *          pullProgress = pullDistance / refreshTargetOffset
   */
  void pullProgress(float pullDistance, float pullProgress);
}

