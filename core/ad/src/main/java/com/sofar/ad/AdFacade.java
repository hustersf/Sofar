package com.sofar.ad;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;

import com.sofar.ad.feed.model.FeedAd;
import com.sofar.ad.log.AdDebug;
import com.sofar.ad.model.AdInfo;
import com.sofar.ad.model.AdPondConfig;
import com.sofar.ad.strategy.ParallelStrategy;
import com.sofar.ad.strategy.SerialStrategy;
import com.sofar.ad.task.business.FeedAdCountTask;
import com.sofar.ad.tt.TTAdCall;
import com.sofar.utility.CollectionUtil;

import io.reactivex.Observable;

public class AdFacade {

  private HashMap<String, AdCall> mAdCallMap = new HashMap<>(4);

  private static final class Inner {
    static AdFacade INSTANCE = new AdFacade();
  }

  private AdFacade() {
    mAdCallMap.put(AdInfo.TT, new TTAdCall());
  }

  public static AdFacade get() {
    return Inner.INSTANCE;
  }

  public AdCall getAdCall(String provider) {
    return mAdCallMap.get(provider);
  }

  public Observable<List<FeedAd>> getFeedAds(@NonNull AdPondConfig.AdPondInfo adPondInfo,
    int count) {
    if (count <= 0 || CollectionUtil.isEmpty(adPondInfo.adInfos)) {
      return Observable.error(new Exception("ad request params error"));
    }

    return createFeedAdObservable(adPondInfo, count);
  }

  private Observable<List<FeedAd>> createFeedAdObservable(
    @NonNull AdPondConfig.AdPondInfo adPondInfo, int count) {
    List<AdInfo> adInfos = adPondInfo.adInfos;
    if (adPondInfo.parallelCount > 1) {
      ParallelStrategy<AdInfo, FeedAd> strategy = new ParallelStrategy<>();
      return strategy.applyStrategy(adInfos, count, adPondInfo.parallelCount,
        info -> new FeedAdCountTask(info, adPondInfo));
    } else {
      SerialStrategy<AdInfo, FeedAd> strategy = new SerialStrategy<>();
      return strategy.applyStrategy(adInfos, count, info -> new FeedAdCountTask(info, adPondInfo));
    }
  }
}
