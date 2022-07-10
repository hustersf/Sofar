package com.sofar.ad.feed.model;

import androidx.annotation.NonNull;

import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.sofar.ad.model.AdType;
import com.sofar.ad.model.BaseAd;

public class FeedAd extends BaseAd {

  public TTFeedAd ttAdData;

  @Override
  public boolean isRenderBySDK() {
    return false;
  }

  @NonNull
  @Override
  public String adType() {
    return AdType.FEED;
  }
}
