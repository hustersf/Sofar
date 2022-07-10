package com.sofar.ad.feed.action;

import android.util.Log;

import com.sofar.ad.feed.data.AdDataParse;
import com.sofar.ad.feed.listener.FeedAdVideoListener;

/**
 * 统一处理视频回调逻辑
 * 如：统一添加埋点
 */
public class FeedAdVideoHelper {

  private static final String TAG = "FeedAdVideoHelper";

  AdDataParse adDataParse;

  public FeedAdVideoHelper(AdDataParse adDataParse) {
    this.adDataParse = adDataParse;
  }

  FeedAdVideoListener proxyVideoListener;

  protected FeedAdVideoListener videoListener = new FeedAdVideoListener() {
    @Override
    public void onVideoStart() {
      Log.d(TAG, "onVideoStart");
      if (proxyVideoListener != null) {
        proxyVideoListener.onVideoStart();
      }
    }

    @Override
    public void onVideoResume() {
      Log.d(TAG, "onVideoResume");
      if (proxyVideoListener != null) {
        proxyVideoListener.onVideoResume();
      }
    }

    @Override
    public void onVideoPause() {
      Log.d(TAG, "onVideoPause");
      if (proxyVideoListener != null) {
        proxyVideoListener.onVideoPause();
      }
    }

    @Override
    public void onVideoStop() {
      Log.d(TAG, "onVideoStop");
      if (proxyVideoListener != null) {
        proxyVideoListener.onVideoStop();
      }
    }

    @Override
    public void onVideoCompleted() {
      Log.d(TAG, "onVideoCompleted");
      if (proxyVideoListener != null) {
        proxyVideoListener.onVideoCompleted();
      }
    }

    @Override
    public void onVideoError() {
      Log.d(TAG, "onVideoError");
      if (proxyVideoListener != null) {
        proxyVideoListener.onVideoError();
      }
    }
  };

  public void setVideoListener() {
    if (adDataParse != null) {
      adDataParse.setVideoAdListener(videoListener);
    }
  }

  public void clearVideoListener() {
    if (adDataParse != null) {
      adDataParse.setVideoAdListener(null);
    }
  }

  public void setProxyVideoListener(FeedAdVideoListener videoListener) {
    this.proxyVideoListener = videoListener;
  }
}
