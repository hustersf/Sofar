package com.sofar.ad.feed.listener;

/**
 * 广告下载回调
 */
public interface FeedAdDownloadListener {

  /**
   * 初始状态
   */
  void onIdle();

  /**
   * @param progress 百分比
   */
  void onProgressUpdate(int progress);

  /**
   * 下载暂停
   */
  void onDownloadPaused();

  /**
   * 下载失败
   */
  void onDownloadFailed();

  /**
   * 下载完成
   */
  void onDownloadFinished();

  /**
   * 安装完成
   */
  void onInstalled();

}
