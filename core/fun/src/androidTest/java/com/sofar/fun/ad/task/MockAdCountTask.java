package com.sofar.fun.ad.task;

import android.util.Log;

import com.sofar.fun.ad.AdInfo;
import com.sofar.fun.ad.MockAd;

import java.util.ArrayList;
import java.util.List;

public class MockAdCountTask extends CountTask<MockAd> {

  AdInfo adInfo;

  public MockAdCountTask(AdInfo adInfo) {
    this.adInfo = adInfo;
  }

  @Override
  public void onExecute() {
    Log.d(MockAd.TAG, "load ad start codeId=" + adInfo.adCodeId() + " adProvider=" + adInfo.adProvider() + " thread=" + Thread.currentThread().getName());
    try {
      Thread.sleep(2000);
      if (adInfo.adCodeId().equals("error")) {
        Log.d(MockAd.TAG, "load ad failed codeId=" + adInfo.adCodeId() + " adProvider=" + adInfo.adProvider());
        error();
      } else {
        Log.d(MockAd.TAG, "load ad success codeId=" + adInfo.adCodeId() + " adProvider=" + adInfo.adProvider());
        List<MockAd> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
          list.add(new MockAd(adInfo.adProvider() + " 的广告标题"));
        }
        for (int i = 0; i < list.size(); i++) {
          Log.d(MockAd.TAG, list.get(i).title);
        }
        result(list);
      }
    } catch (Exception e) {
      e.printStackTrace();
      Log.d(MockAd.TAG, "e=" + e.getMessage());
    }
  }
}
