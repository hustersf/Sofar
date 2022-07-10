package com.sofar.ad.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.os.SystemClock;
import androidx.annotation.NonNull;

import com.sofar.ad.feed.model.FeedAd;
import com.sofar.ad.model.AdInfo;
import com.sofar.ad.model.BaseAd;

/**
 * 存广告
 * 1.并行策略加载剩余的广告
 * 2.预加载的广告
 * <p>
 * 取广告
 * 1.减少广告浪费(多余的广告如果直接丢弃,展示率会降低,可能影响广告平台的请求成功率)
 * 2.提升广告请求速度
 */
public class CachedAdManager {
  //广告有效时间，40min
  private static final long AD_EXPIRED_DURATION = 40 * 60 * 1000;

  private Map<String, CacheAd<FeedAd>> mCachedFeedAd = new HashMap<>();

  private static final class Inner {
    static CachedAdManager INSTANCE = new CachedAdManager();
  }

  private CachedAdManager() {}

  public static CachedAdManager get() {
    return Inner.INSTANCE;
  }

  private String key(@NonNull AdInfo adInfo) {
    return adInfo.adProvider + "_" + adInfo.adCodeId;
  }

  public void putFeedAd(@NonNull AdInfo adInfo, @NonNull List<FeedAd> feedAds) {
    String key = key(adInfo);
    // 根据codeId缓存
    CacheAd<FeedAd> cacheFeedAd = mCachedFeedAd.get(key);
    if (cacheFeedAd == null) {
      cacheFeedAd = new CacheAd();
      mCachedFeedAd.put(key, cacheFeedAd);
    }

    for (FeedAd ad : feedAds) {
      cacheFeedAd.add(ad);
    }
  }

  @NonNull
  public List<FeedAd> getFeedAd(@NonNull AdInfo adInfo, int count) {
    String key = key(adInfo);
    CacheAd<FeedAd> cacheAd = mCachedFeedAd.get(key);
    List<FeedAd> list = new ArrayList<>(count);
    if (cacheAd != null) {
      loadAdFromCache(count, list, cacheAd, adInfo);
    }
    return list;
  }

  private <T extends BaseAd> void loadAdFromCache(int count, @NonNull List<T> resultAds,
    @NonNull CacheAd<T> cache, @NonNull AdInfo adInfo) {
    for (Iterator<Map.Entry<Long, T>> it = cache.ads.entrySet().iterator(); it.hasNext(); ) {
      Map.Entry<Long, T> item = it.next();

      long ttl = AD_EXPIRED_DURATION;
      if (adInfo.ttl > 0) {
        ttl = adInfo.ttl;
      }
      if (isAdNotExpired(item.getKey(), ttl)) {
        // 如果广告未过期
        item.getValue().fromCache = true;
        resultAds.add(item.getValue());
      }
      it.remove();

      // 如果缓存的广告满足了请求数量
      if (resultAds.size() == count) {
        return;
      }
    }
  }

  private boolean isAdNotExpired(long cacheTime, long ttl) {
    return SystemClock.elapsedRealtime() + 1000 < (cacheTime + ttl);
  }

  /**
   * 清理缓存
   */
  public void clear() {
    mCachedFeedAd.clear();
  }
}
