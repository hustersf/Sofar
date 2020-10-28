package com.sofar.download;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

public interface DownloadListener {

  /**
   * 下载已建立好连接，即将开始下载
   */
  @MainThread
  default void onDownloadConnected(@NonNull DownloadRequest request) {
  }

  /**
   * 下载请求超过最大限制，当前请求进图等待状态
   */
  @MainThread
  default void onDownloadWait(@NonNull DownloadRequest request) {
  }

  /**
   * 下载被取消
   */
  @MainThread
  default void onDownloadCanceled(@NonNull DownloadRequest request) {
  }

  /**
   * 下载完成
   */
  @MainThread
  default void onDownloadComplete(@NonNull DownloadRequest request) {
  }

  /**
   * 下载失败
   */
  @MainThread
  default void onDownloadFailed(@NonNull DownloadRequest request, int code, String message) {
  }

  /**
   * 下载进度
   */
  @MainThread
  default void onDownloadProgress(@NonNull DownloadRequest request, long totalBytes, long downloadedBytes, int progress) {
  }

}
