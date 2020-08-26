package com.sofar.fun.badge;

/**
 * 小红点可以抽象为一个树型结构，如图
 * <p>
 * -------------[A,C] [H,I] [X,Z]
 * --------------------1
 * -----------[A,C]  [H,I]    [X,Z]
 * ------------2       3        4
 * ----------A,B,C    H,I     X,Y,Z
 * <p>
 * 第三层级的为 叶子节点，表示某个具体的消息
 * 第一和第二层级 表示一个消息的集合
 * <p>
 * 树中各个节点的红点数计算方式为
 * 节点2=A+B+C
 * 节点1=节点2+节点3+节点4
 * <p>
 * BadgeNumber表示树中的一个叶子节点
 */
public class BadgeNumber {

  /**
   * 消息大类别
   */
  public final static int KIND_X = 0x01;
  public final static int KIND_NEWS = 0x02;
  public final static int KIND_Z = 0x03;

  /**
   * 属于KIND_X的消息
   */
  public final static int TYPE_X1 = (KIND_X << 16) + 0x01;
  public final static int TYPE_X2 = (KIND_X << 16) + 0x02;

  /**
   * 属于KIND_NEWS的消息
   */
  public static final int TYPE_COMMENT = (KIND_NEWS << 16) + 0x1;
  public static final int TYPE_LIKED = (KIND_NEWS << 16) + 0x2;
  public static final int TYPE_FOLLOW = (KIND_NEWS << 16) + 0x3;


  /**
   * 在父节点显示方式是：（红）点。
   */
  public static final int DISPLAY_MODE_ON_PARENT_DOT = 0;
  /**
   * 在父节点显示方式是：数字。
   */
  public static final int DISPLAY_MODE_ON_PARENT_NUMBER = 1;

  public int type;//badge number真正的类型
  public int count;//badge number的count
  public int displayMode;//当前badge number在父节点上的显示方式

}
