package com.sofar.widget.nested;

import android.view.View;
import androidx.annotation.NonNull;

/**
 * 处理嵌套滑动子View之间的联动
 */
public interface NestedLinkScrollChild {

  /**
   * 子View A 快速fling，但是在fling产生的距离消耗完之前，A已经不能在滚动了
   * 此时子View B 需要消费掉剩下的fling，从而保证fling的连贯
   */
  boolean fling(int velocityY);

  /**
   * 滚动到顶部
   */
  void scrollToTop();

  /**
   * 滚动到底部
   */
  void scrollToBottom();

  /**
   * 停止滚动
   */
  void stopScroll();

  int computeVerticalScrollRange();

  int computeVerticalScrollOffset();

  /**
   * 联动滚动监听
   */
  interface OnNestedScrollListener {

    void onNestedScrollStateChanged(@NonNull View target, int newState);

    void onNestedScrolled(@NonNull View target, int dx, int dy);

  }

  void setOnNestedScrollListener(@NonNull OnNestedScrollListener listener);

}
