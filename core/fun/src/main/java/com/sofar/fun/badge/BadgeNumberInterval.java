package com.sofar.fun.badge;

/**
 * 一个区间包含n个叶子节点
 * 树中的非叶子节点，包含n个区间
 * n>=1
 * <p>
 * BadgeNumberInterval 表示一个区间
 * {@link #typeMin},表示区间中type值最小的
 * {@link #typeMax},表示区间中type值最大的
 * 区间内所有红点 {@link BadgeNumber#type}的值，应该在[typeMin,typeMax]之间
 */
public class BadgeNumberInterval {

  public int typeMin;
  public int typeMax;

}
