package com.sofar.ad.feed.action;

import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sofar.ad.feed.data.AdDataParse;
import com.sofar.ad.feed.listener.FeedAdDownloadListener;

/**
 * 统一处理广告下载逻辑
 * 如：统一添加埋点
 */
public class FeedAdDownloadHelper {

  private static final String TAG = "FeedAdDownloadHelper";

  AdDataParse adDataParse;

  public FeedAdDownloadHelper(AdDataParse adDataParse) {
    this.adDataParse = adDataParse;
  }


  FeedAdDownloadListener downloadListener = new FeedAdDownloadListener() {
    @Override
    public void onIdle() {
      // 初始状态
      if (adDataParse != null) {
        Log.d(TAG, "onIdle " + adDataParse.getAdProvider() + ":" + adDataParse.getTitle());
      }
    }

    @Override
    public void onProgressUpdate(int progress) {
      //下载状态

    }

    @Override
    public void onDownloadPaused() {
      //暂停状态
    }

    @Override
    public void onDownloadFailed() {
      //重新下载状态
    }

    @Override
    public void onDownloadFinished() {
      //下载完成
    }

    @Override
    public void onInstalled() {
      //安装完成
    }
  };

  /**
   * 传入下载按钮控件
   * 可统一处理 下载按钮在不同状态下的UI更新
   */
  public void setDownloadListener(@NonNull View buttonProgress) {
    downloadListenerInner();
  }

  /**
   * 同 {@link #setDownloadListener(View)}
   * 另一种场景
   * 🤔 此类处理 UI 逻辑是否合适
   */
  public void setDialogDownloadListener(@NonNull ProgressBar downloadProgress,
    @NonNull TextView buttonText) {
    downloadListenerInner();
  }


  public void clearDownloadListener() {
    if (adDataParse != null) {
      adDataParse.setDownloadListener(null);
      Log.d(TAG, "clearDownloadListener " + adDataParse.getAdProvider() + ":"
        + adDataParse.getTitle());
    }
  }

  private void downloadListenerInner() {
    if (adDataParse != null) {
      Log.d(TAG, "setDownloadListener " + adDataParse.getAdProvider() + ":"
          + adDataParse.getTitle());
      adDataParse.setDownloadListener(downloadListener);
    }
  }


}
