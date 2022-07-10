package com.sofar.ad.feed.listener;

/**
 * 广告行为回调
 */
public interface FeedAdInteractionListener {

  void onAdShow();

  void onAdClick();

  void onAdCreativeClick();

  void onAdClose();

  void onAdError(int code, String msg);

}
