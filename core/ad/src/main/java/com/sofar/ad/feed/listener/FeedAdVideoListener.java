package com.sofar.ad.feed.listener;

/**
 * 视频回调
 */
public interface FeedAdVideoListener {
  void onVideoStart();

  void onVideoResume();

  void onVideoPause();

  void onVideoStop();

  void onVideoCompleted();

  void onVideoError();
}
