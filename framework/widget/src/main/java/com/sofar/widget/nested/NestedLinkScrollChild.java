package com.sofar.widget.nested;

/**
 * 处理嵌套滑动子View之间的联动
 */
public interface NestedLinkScrollChild {

  /**
   * 子View A 快速fling，但是在fling产生的距离消耗完之前，A已经不能在滚动了
   * 此时子View B 需要消费掉剩下的fling，从而保证fling的连贯
   */
  boolean fling(int velocityY);

}
