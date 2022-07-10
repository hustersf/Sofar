package com.sofar.ad.feed.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.sofar.ad.feed.listener.FeedAdDownloadListener;
import com.sofar.ad.feed.listener.FeedAdInteractionListener;
import com.sofar.ad.feed.listener.FeedAdVideoListener;

/**
 * 抽象出广告展示元素和行为
 * 屏蔽各家广告SDK API差异
 */
public interface AdDataParse {

  /**
   * 广告供应方
   */
  String getAdProvider();

  /**
   * 广告标题
   */
  String getTitle();

  /**
   * 广告标签后面的文案
   */
  String getDes();

  /**
   * app图标url
   */
  String getIconUrl();

  /**
   * 图片urls
   */
  List<String> getImageUrls();

  /**
   * 广告素材宽
   */
  int getWidth();

  /**
   * 广告素材高
   */
  int getHeight();

  /**
   * 按钮文案
   */
  String getButtonText();

  /**
   * 广告监听
   */
  void registerViewForInteraction(@NonNull ViewGroup parent, @NonNull List<View> clickViewList,
    @Nullable List<View> creativeViewList, FeedAdInteractionListener listener);

  default void unregisterView() {

  }

  /**
   * 视频监听
   */
  void setVideoAdListener(FeedAdVideoListener listener);

  /**
   * 下载监听
   */
  void setDownloadListener(FeedAdDownloadListener listener);

}
