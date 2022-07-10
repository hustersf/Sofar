package com.sofar.ad.tt;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;

import com.sofar.ad.AdCall;
import com.sofar.ad.AdRequest;
import com.sofar.ad.feed.model.FeedAd;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class TTAdCall extends AdCall {

  @Override
  public void loadFeedAdFromSdk(@NonNull AdRequest request, @NonNull AdFeedListener listener) {
    //todo 替换成真正的SDK请求
    long delay = new Random().nextInt(1000);
    int count = new Random().nextInt(request.count + 1);
    Observable.timer(delay, TimeUnit.MILLISECONDS)
      .observeOn(AndroidSchedulers.mainThread())
      .doOnComplete(() -> {
        List<FeedAd> list = new ArrayList<>();

        for (int i = 0; i < count; i++) {
          FeedAd ad = new FeedAd();
          list.add(ad);
        }
        listener.onFeedAdLoad(list);
      }).subscribe();
  }
}
