package com.sofar.ad.feed.action;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.sofar.ad.feed.data.AdDataParse;
import com.sofar.ad.feed.listener.FeedAdInteractionListener;

/**
 * 统一处理广告行为
 * 如：统一添加埋点
 */
public class FeedAdInteractionHelper {

  private static final String TAG = "FeedAdInteractionHelper";

  AdDataParse adDataParse;

  public FeedAdInteractionHelper(AdDataParse adDataParse) {
    this.adDataParse = adDataParse;
  }

  protected FeedAdInteractionListener adInteractionListener = new FeedAdInteractionListener() {
    @Override
    public void onAdShow() {
      if (adDataParse != null) {
        Log.d(TAG, "onAdShow:" + adDataParse.getTitle());
      }
    }

    @Override
    public void onAdClick() {
      if (adDataParse != null) {
        Log.d(TAG, "onAdClick:" + adDataParse.getTitle());
      }
    }

    @Override
    public void onAdCreativeClick() {
      if (adDataParse != null) {
        Log.d(TAG, "onAdCreativeClick:" + adDataParse.getTitle());
      }
    }

    @Override
    public void onAdClose() {
      if (adDataParse != null) {
        Log.d(TAG, "onAdClose:" + adDataParse.getTitle());
      }
    }

    @Override
    public void onAdError(int code, String msg) {
      if (adDataParse != null) {
        Log.d(TAG, "onAdError:" + adDataParse.getTitle());
      }
    }
  };

  public void registerViewForInteraction(@NonNull ViewGroup parent,
    @NonNull List<View> clickViewList, @Nullable List<View> creativeViewList) {
    if (adDataParse != null) {
      adDataParse.registerViewForInteraction(parent, clickViewList,
        creativeViewList, adInteractionListener);
      Log.d(TAG, "registerViewForInteraction=" + adDataParse.getAdProvider() + ":"
        + adDataParse.getTitle());
    }
  }

  public void unregisterView() {
    if (adDataParse != null) {
      adDataParse.unregisterView();
    }
  }

}
