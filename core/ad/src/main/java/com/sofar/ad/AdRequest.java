package com.sofar.ad;

import com.sofar.ad.model.AdInfo;

public class AdRequest {
  public int count;  //请求条数
  public AdInfo adInfo;  //请求的广告位信息
  public String cid;  //频道
  public boolean preload;  //是否预加载广告
  public int preloadCount; //预加载的广告条数

  private AdRequest() {}

  public static class Builder {
    private int count;  //请求条数
    private AdInfo adInfo; //请求的广告位信息
    private String cid;  //频道
    private boolean preload;  //本次是否预加载广告
    private int preloadCount; //预加载的广告条数

    public Builder setCount(int count) {
      this.count = count;
      return this;
    }

    public Builder setAdInfo(AdInfo adInfo) {
      this.adInfo = adInfo;
      return this;
    }

    public Builder setCid(String cid) {
      this.cid = cid;
      return this;
    }

    public Builder setPreload(boolean preload) {
      this.preload = preload;
      return this;
    }

    public Builder setPreloadCount(int preloadCount) {
      this.preloadCount = preloadCount;
      return this;
    }

    public AdRequest build() {
      AdRequest request = new AdRequest();
      request.count = count;
      request.adInfo = adInfo;
      request.cid = cid;
      request.preload = preload;
      request.preloadCount = preloadCount;
      return request;
    }
  }

}
