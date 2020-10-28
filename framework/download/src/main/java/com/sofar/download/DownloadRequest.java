package com.sofar.download;

import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

/**
 * 创建一个下载请求
 */
public class DownloadRequest {

  /**
   * 文件下载地址
   */
  @NonNull
  public Uri uri;

  /**
   * 请求头信息
   */
  @NonNull
  public HashMap<String, String> headers = new HashMap<>();

  /**
   * 自动生成
   */
  public int downloadId;

  private boolean canceled;
  private int downloadStatus;

  /**
   * 文件名字，默认取uri的尾部
   */
  @NonNull
  public String fileName;

  /**
   * 文件保存目录，默认取 {@link DownloadConfig#downloadDir}
   */
  @NonNull
  public String fileDirPath;

  @Nullable
  DownloadListener downloadListener;


  public DownloadRequest(@NonNull Uri uri) {
    this.uri = uri;
    String scheme = uri.getScheme();
    if (!isHttp(scheme)) {
      throw new IllegalArgumentException("can only download http/https uri: " + uri);
    }

    fileName = uri.getLastPathSegment();
    if (fileName == null) {
      throw new IllegalArgumentException("uri error due to fileName==null");
    }

    fileDirPath = DownloadManager.get().getConfig().downloadDir.getAbsolutePath();
  }

  public void addHeaders(@NonNull HashMap<String, String> map) {
    headers.putAll(map);
  }

  void setDownloadId(int downloadId) {
    this.downloadId = downloadId;
  }

  public void setFileName(@NonNull String fileName) {
    this.fileName = fileName;
  }

  public void setFileDirPath(@NonNull String fileDirPath) {
    this.fileDirPath = fileDirPath;
  }

  public void setDownloadListener(DownloadListener listener) {
    this.downloadListener = listener;
  }

  private boolean isHttp(String scheme) {
    return TextUtils.equals(scheme, "http") || TextUtils.equals(scheme, "https");
  }

  boolean isCanceled() {
    return canceled;
  }

  void cancel() {
    Util.runOnUiThread(() -> {
      canceled = true;
      downloadStatus = DownloadStatus.CANCELED;
      if (downloadListener != null) {
        downloadListener.onDownloadCanceled(this);
      }
    });
  }

  void connect() {
    Util.runOnUiThread(() -> {
      downloadStatus = DownloadStatus.CONNECTED;
      if (downloadListener != null) {
        downloadListener.onDownloadConnected(this);
      }
    });
  }

  void error(int code, String message) {
    Util.runOnUiThread(() -> {
      downloadStatus = DownloadStatus.FAILED;
      if (downloadListener != null) {
        downloadListener.onDownloadFailed(this, code, message);
      }
    });
  }

  void progress(long totalBytes, long downloadedBytes, int progress) {
    Util.runOnUiThread(() -> {
      downloadStatus = DownloadStatus.PROGRESS;
      if (downloadListener != null) {
        downloadListener.onDownloadProgress(this, totalBytes, downloadedBytes, progress);
      }
    });
  }

  void complete() {
    Util.runOnUiThread(() -> {
      downloadStatus = DownloadStatus.COMPLETE;
      DownloadDispatcher.get().finished(this);
      if (downloadListener != null) {
        downloadListener.onDownloadComplete(this);
      }
    });
  }

  void waiting() {
    Util.runOnUiThread(() -> {
      downloadStatus = DownloadStatus.WAIT;
      if (downloadListener != null) {
        downloadListener.onDownloadWait(this);
      }
    });
  }

  @DownloadStatus
  int getStatus() {
    return downloadStatus;
  }

}
