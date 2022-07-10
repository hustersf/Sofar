package com.sofar.ad.feed.data;

import java.util.Collections;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sofar.ad.feed.listener.FeedAdDownloadListener;
import com.sofar.ad.feed.listener.FeedAdInteractionListener;
import com.sofar.ad.feed.listener.FeedAdVideoListener;
import com.sofar.ad.feed.model.FeedAd;

public class AdDataParseFactory {

  @NonNull
  public static AdDataParse buildAdDataParse(@NonNull FeedAd feedAd) {
    if (feedAd.ttAdData != null) {
      return new TTAdDataParse(feedAd.ttAdData);
    }
    return DEFAULT;
  }

  private static AdDataParse DEFAULT = new AdDataParse() {
    @Override
    public String getAdProvider() {
      return "";
    }

    @Override
    public String getTitle() {
      return "";
    }

    @Override
    public String getDes() {
      return "";
    }

    @Override
    public String getIconUrl() {
      return "";
    }

    @Override
    public List<String> getImageUrls() {
      return Collections.emptyList();
    }

    @Override
    public int getWidth() {
      return 0;
    }

    @Override
    public int getHeight() {
      return 0;
    }

    @Override
    public String getButtonText() {
      return "";
    }

    @Override
    public void registerViewForInteraction(@NonNull ViewGroup parent,
      @NonNull List<View> clickViewList, @Nullable List<View> creativeViewList,
      FeedAdInteractionListener listener) {

    }

    @Override
    public void setVideoAdListener(FeedAdVideoListener listener) {

    }

    @Override
    public void setDownloadListener(FeedAdDownloadListener listener) {

    }
  };


}
