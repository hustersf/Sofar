package com.sofar.ad;

import java.util.List;

import android.os.SystemClock;
import androidx.annotation.NonNull;

import com.sofar.ad.cache.CachedAdManager;
import com.sofar.ad.feed.model.FeedAd;
import com.sofar.ad.log.AdDebug;
import com.sofar.ad.util.AdUtil;

public abstract class AdCall {

  public void loadFeedAd(@NonNull AdRequest request, AdFeedListener listener) {
    if (request.adInfo == null || request.count <= 0) {
      if (listener != null) {
        listener.onError(-1, "AdRequest params error");
      }
      return;
    }

    List<FeedAd> resultAds = CachedAdManager.get().getFeedAd(request.adInfo, request.count);
    if (resultAds.size() == request.count) {
      AdDebug.d("loadFeedAd:adInfo=%s,从缓存中取出了%d条广告,数量满足要求直接返回结果",
        AdUtil.printAdInfo(request.adInfo), resultAds.size());
      if (listener != null) {
        listener.onFeedAdLoad(resultAds);
      }
      return;
    }

    request.count = request.count - resultAds.size();
    AdDebug.d("loadFeedAd:adInfo=%s,从缓存中取出了%d条广告,还需要请求%d条广告",
      AdUtil.printAdInfo(request.adInfo), resultAds.size(), request.count);
    loadFeedAdFromSdk(request, new AdFeedListener() {
      @Override
      public void onError(int code, String message) {
        if (listener != null) {
          if (!resultAds.isEmpty()) {
            listener.onFeedAdLoad(resultAds);
          } else {
            listener.onError(code, message);
          }
        }
      }

      @Override
      public void onFeedAdLoad(@NonNull List<FeedAd> ads) {
        long t = SystemClock.elapsedRealtime();
        int i = 0;
        for (FeedAd ad : ads) {
          ad.firstLoadTime = t + (i++);
          ad.adInfo = request.adInfo;
        }
        if (listener != null) {
          resultAds.addAll(ads);
          listener.onFeedAdLoad(resultAds);
        }
      }
    });
  }

  public abstract void loadFeedAdFromSdk(@NonNull AdRequest request,
    @NonNull AdFeedListener listener);

  public interface AdFeedListener {
    void onError(int code, String message);

    void onFeedAdLoad(@NonNull List<FeedAd> ads);
  }
}
