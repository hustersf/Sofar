package com.sofar.ad.model;

import java.util.List;

public class AdPondConfig {

  public List<AdPondInfo> adPondInfos;

  public static class AdPondInfo {

    public int count;  //广告请求条数

    public List<AdInfo> adInfos;  //广告位信息，包含多家sdk

    public List<AdInfo> dailyFirstAdInfos;  //额外的广告配置

    public List<AdInfo> rtbAdInfos;  //额外的广告配置

    public int parallelCount;  //广告请求并发数

    public int preloadCount;  //预加载的广告条数
  }
}
