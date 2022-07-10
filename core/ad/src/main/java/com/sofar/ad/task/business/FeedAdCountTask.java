package com.sofar.ad.task.business;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

import com.sofar.ad.AdCall;
import com.sofar.ad.AdFacade;
import com.sofar.ad.AdRequest;
import com.sofar.ad.cache.CachedAdManager;
import com.sofar.ad.feed.model.FeedAd;
import com.sofar.ad.log.AdDebug;
import com.sofar.ad.model.AdInfo;
import com.sofar.ad.model.AdPondConfig;
import com.sofar.ad.util.AdUtil;
import com.sofar.utility.CollectionUtil;

/**
 * 请求feed类型的广告
 */
public class FeedAdCountTask extends BaseAdTask<FeedAd> {

  @NonNull
  private AdPondConfig.AdPondInfo mAdPondInfo;

  public FeedAdCountTask(@NonNull AdInfo adInfo, @NonNull AdPondConfig.AdPondInfo adPondInfo) {
    super(adInfo);
    mAdPondInfo = adPondInfo;
  }

  @Override
  public void onExecute() {
    AdRequest request = new AdRequest.Builder()
      .setCount(count)
      .setAdInfo(mAdInfo)
      .setPreload(false)
      .setPreloadCount(mAdPondInfo.preloadCount)
      .build();

    AdCall adCall = AdFacade.get().getAdCall(mAdInfo.adProvider);
    if (adCall == null) {
      postError();
      return;
    }

    AdDebug.d("start load FeedAd:adInfo=%s,count=%d,preloadCount=%d,",
      AdUtil.printAdInfo(mAdInfo), count, mAdPondInfo.preloadCount);
    adCall.loadFeedAd(request, new AdCall.AdFeedListener() {
      @Override
      public void onError(int code, String message) {
        postError();
        AdDebug.d("load FeedAd error:adInfo=%s,code=%d,message=%s",
          AdUtil.printAdInfo(mAdInfo), code, message);
      }

      @Override
      public void onFeedAdLoad(List<FeedAd> ads) {
        List<FeedAd> resultAds = new ArrayList<>(ads);
        AdDebug.d("load FeedAd success:adInfo=%s,返回%d条广告",
          AdUtil.printAdInfo(mAdInfo), resultAds.size());
        if (!CollectionUtil.isEmpty(ads)) {
          for (FeedAd ad : ads) {
            ad.adInfo = mAdInfo;
          }
          // 如果拉取到的广告数量比需要的数量多，则将剩余的广告放入缓存
          if (resultAds.size() > count) {
            abandon(resultAds.subList(count, resultAds.size()));
            resultAds = resultAds.subList(0, count);
          }
        }
        AdDebug.d("adInfo=%s收到%d条广告", AdUtil.printAdInfo(mAdInfo), resultAds.size());
        postResult(resultAds);
      }
    });
  }

  @Override
  public void abandon(@NonNull List<FeedAd> list) {
    super.abandon(list);
    AdDebug.d("adInfo=%s剩余%d条广告放入缓存", AdUtil.printAdInfo(mAdInfo), list.size());
    CachedAdManager.get().putFeedAd(mAdInfo, list);
  }
}
