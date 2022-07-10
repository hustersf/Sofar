package com.sofar.ad.task.business;

import java.util.List;

import androidx.annotation.NonNull;

import com.sofar.ad.model.AdInfo;
import com.sofar.ad.model.BaseAd;
import com.sofar.ad.task.CountTask;

public abstract class BaseAdTask<T extends BaseAd> extends CountTask<T> {

  @NonNull
  protected AdInfo mAdInfo;

  public BaseAdTask(@NonNull AdInfo adInfo) {
    mAdInfo = adInfo;
  }

  @Override
  public void postError() {
    super.postError();
  }

  @Override
  public void postResult(@NonNull List<T> results) {
    super.postResult(results);
  }
}
