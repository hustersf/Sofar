package com.sofar.ad;

import java.util.List;

import com.sofar.ad.feed.model.FeedAd;

public class AdResponse {

  public List<FeedAd> feedAds;
  public int code;

  public boolean isSuccessful() {
    return code == 0;
  }

  public static class Builder {

    private List<FeedAd> feedAds;
    private int code;

    public Builder setAds(List<FeedAd> ads) {
      feedAds = ads;
      return this;
    }

    public Builder setCode(int code) {
      this.code = code;
      return this;
    }

    public AdResponse build() {
      AdResponse response = new AdResponse();
      response.feedAds = feedAds;
      response.code = code;
      return response;
    }
  }
}
