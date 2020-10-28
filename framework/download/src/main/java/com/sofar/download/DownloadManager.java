package com.sofar.download;

import android.app.Application;

import androidx.annotation.NonNull;

/**
 * 一定要先调用init方法
 */
public class DownloadManager {

  public final static String TAG = "DownloadManager";

  @NonNull
  private DownloadConfig config;
  @NonNull
  private Application appContext;

  private static class Inner {
    private static DownloadManager INSTANCE = new DownloadManager();
  }

  private DownloadManager() {
  }

  public static DownloadManager get() {
    return Inner.INSTANCE;
  }

  public void init(@NonNull Application appContext, @NonNull DownloadConfig config) {
    this.appContext = appContext;
    this.config = config;
    if (config.downloadDir == null) {
      config.downloadDir = Util.getCacheDir(appContext);
    }
  }

  @NonNull
  public DownloadConfig getConfig() {
    return config;
  }

  @NonNull
  public Application getAppContext() {
    return appContext;
  }

  /**
   * 发起一个下载请求，并给此请求分配一个唯一id返回
   */
  public int add(@NonNull DownloadRequest request) {
    return DownloadDispatcher.get().enqueue(request);
  }

  /**
   * 取消downloadId对应的下载请求
   */
  public void cancel(int downloadId) {
    DownloadDispatcher.get().cancel(downloadId);
  }

  /**
   * 取消所有的下载请求
   */
  public void cancelAll() {
    DownloadDispatcher.get().cancelAll();
  }

  /**
   * 返回downloadId对应的下载 当前的状态
   */
  @DownloadStatus
  public int query(int downloadId) {
    return DownloadDispatcher.get().query(downloadId);
  }

}
