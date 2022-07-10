package com.sofar.ad.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 跟踪整个广告的请求过程
 */
public class AdTrace {

  public List<ChildTrace> traces = Collections.synchronizedList(new ArrayList<>());

  @AdStrategy
  public int strategy; //记录使用的请求策略

  public int insertCount;

  public int requestCount;

  public int parallelCount;

  /**
   * 记录每一层广告的请求信息
   */
  public static class ChildTrace {

    public long time;  //耗时

    public int status; // 0成功，1失败

    public String msg;

    public AdInfo adInfo;  //对应的广告位
  }

  public @interface AdStrategy {
    int PARALLEL = 1;
    int SEQUENTIAL = 2;
  }

}
