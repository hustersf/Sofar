package com.sofar.ad.util;

import java.util.UUID;

import androidx.annotation.NonNull;

import com.google.common.primitives.UnsignedLong;
import com.sofar.ad.AdRequest;
import com.sofar.ad.model.AdInfo;
import com.sofar.ad.model.BaseAd;
import com.sofar.ad.util.hash.TermSign;

public class AdUtil {

  public static String randomId() {
    try {
      String uuId = UUID.randomUUID().toString();
      long sign = TermSign.calcTermSign(uuId.getBytes());
      return UnsignedLong.fromLongBits(sign).toString();
    } catch (Exception e) {
      return String.valueOf(System.currentTimeMillis());
    }
  }

  public static String printAdInfo(@NonNull BaseAd baseAd) {
    if (baseAd.adInfo != null) {
      return printAdInfo(baseAd.adInfo);
    }
    return "[没有adinfo]";
  }

  public static String printAdInfo(@NonNull AdInfo adInfo) {
    return "[" + adInfo.adProvider + ":" + adInfo.adCodeId + "]";
  }

}
