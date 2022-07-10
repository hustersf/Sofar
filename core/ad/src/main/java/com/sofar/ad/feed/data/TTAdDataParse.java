package com.sofar.ad.feed.data;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.sofar.ad.feed.listener.FeedAdDownloadListener;
import com.sofar.ad.feed.listener.FeedAdInteractionListener;
import com.sofar.ad.feed.listener.FeedAdVideoListener;

/**
 * 穿山甲广告数据解析和广告行为
 */
public class TTAdDataParse implements AdDataParse {

  @NonNull final TTFeedAd mTTFeedAd;

  public TTAdDataParse(@NonNull TTFeedAd ttFeedAd) {
    mTTFeedAd = ttFeedAd;
  }

  @Override
  public String getAdProvider() {
    return "TT";
  }

  @Override
  public String getTitle() {
    return mTTFeedAd.getTitle();
  }

  @Override
  public String getDes() {
    return mTTFeedAd.getDescription();
  }

  @Override
  public String getIconUrl() {
    return mTTFeedAd.getIcon().getImageUrl();
  }

  @Override
  public List<String> getImageUrls() {
    List<String> list = new ArrayList<>();
    for (TTImage image : mTTFeedAd.getImageList()) {
      list.add(image.getImageUrl());
    }
    return list;
  }

  @Override
  public int getWidth() {
    TTImage image = mTTFeedAd.getImageList().get(0);
    return image.getWidth();
  }

  @Override
  public int getHeight() {
    TTImage image = mTTFeedAd.getImageList().get(0);
    return image.getHeight();
  }

  @Override
  public String getButtonText() {
    return mTTFeedAd.getButtonText();
  }

  @Override
  public void registerViewForInteraction(
    @NonNull ViewGroup parent,
    @NonNull List<View> clickViewList,
    @Nullable List<View> creativeViewList,
    FeedAdInteractionListener listener) {
    mTTFeedAd.registerViewForInteraction(parent, clickViewList, creativeViewList,
      new TTNativeAd.AdInteractionListener() {
        @Override
        public void onAdClicked(View view, TTNativeAd ttNativeAd) {
          if (listener != null) {
            listener.onAdClick();
          }
        }

        @Override
        public void onAdCreativeClick(View view, TTNativeAd ttNativeAd) {
          if (listener != null) {
            listener.onAdCreativeClick();
          }
        }

        @Override
        public void onAdShow(TTNativeAd ttNativeAd) {
          if (listener != null) {
            listener.onAdShow();
          }
        }
      });
  }

  @Override
  public void setVideoAdListener(FeedAdVideoListener listener) {
    mTTFeedAd.setVideoAdListener(new TTFeedAd.VideoAdListener() {
      @Override
      public void onVideoLoad(TTFeedAd ttFeedAd) {

      }

      @Override
      public void onVideoError(int errorCode, int extraCode) {
        if (listener != null) {
          listener.onVideoError();
        }
      }

      @Override
      public void onVideoAdStartPlay(TTFeedAd ttFeedAd) {
        if (listener != null) {
          listener.onVideoStart();
        }
      }

      @Override
      public void onVideoAdPaused(TTFeedAd ttFeedAd) {
        if (listener != null) {
          listener.onVideoPause();
        }
      }

      @Override
      public void onVideoAdContinuePlay(TTFeedAd ttFeedAd) {
        if (listener != null) {
          listener.onVideoResume();
        }
      }

      @Override
      public void onProgressUpdate(long l, long l1) {

      }

      @Override
      public void onVideoAdComplete(TTFeedAd ttFeedAd) {
        if (listener != null) {
          listener.onVideoCompleted();
        }
      }
    });
  }

  @Override
  public void setDownloadListener(FeedAdDownloadListener listener) {
    mTTFeedAd.setDownloadListener(new TTAppDownloadListener() {
      @Override
      public void onIdle() {
        if (listener != null) {
          listener.onIdle();
        }
      }

      @Override
      public void onDownloadActive(long totalBytes, long currBytes, String fileName,
        String appName) {
        int progress = 0;
        if (totalBytes > 0) {
          progress = (int) (currBytes * 100 / totalBytes);
        }

        if (listener != null) {
          listener.onProgressUpdate(progress);
        }
      }

      @Override
      public void onDownloadPaused(long totalBytes, long currBytes, String fileName,
        String appNam) {
        if (listener != null) {
          listener.onDownloadPaused();
        }
      }

      @Override
      public void onDownloadFailed(long totalBytes, long currBytes, String fileName,
        String appNam) {
        if (listener != null) {
          listener.onDownloadFailed();
        }
      }

      @Override
      public void onDownloadFinished(long totalBytes, String fileName, String appName) {
        if (listener != null) {
          listener.onDownloadFinished();
        }
      }

      @Override
      public void onInstalled(String fileName, String appName) {
        if (listener != null) {
          listener.onInstalled();
        }
      }
    });
  }
}
