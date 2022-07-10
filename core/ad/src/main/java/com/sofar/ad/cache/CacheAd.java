package com.sofar.ad.cache;

import java.util.Map;
import java.util.TreeMap;

import com.sofar.ad.log.AdDebug;
import com.sofar.ad.model.BaseAd;
import com.sofar.ad.util.AdUtil;

public class CacheAd<Ad extends BaseAd> {

  public final Map<Long, Ad> ads;

  public CacheAd() {
    this.ads = new TreeMap<>();
  }

  public void add(Ad ad) {
    ads.put(ad.firstLoadTime, ad);
    AdDebug.d("adInfo=%s,将广告加入缓存,缓存时间=%d", AdUtil.printAdInfo(ad), ad.firstLoadTime);
  }
}
