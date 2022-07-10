package com.sofar.ad.model;

import androidx.annotation.NonNull;

import com.sofar.ad.util.AdUtil;

public abstract class BaseAd {

  public String adId;  //广告唯一标识
  public AdInfo adInfo; // 真正填充的广告信息
  public boolean endAd; // 是否是保底的广告

  public boolean fromCache = false;
  public long firstLoadTime = 0;

  // 广告的ecpm值
  public int ecpm;

  public BaseAd() {
    adId = AdUtil.randomId();
  }

  /**
   * 自渲染 or SDK渲染
   */
  public abstract boolean isRenderBySDK();

  public String getCodeId() {
    return adInfo == null ? "NULL" : adInfo.adCodeId;
  }

  @NonNull
  @AdType
  public abstract String adType();

}
