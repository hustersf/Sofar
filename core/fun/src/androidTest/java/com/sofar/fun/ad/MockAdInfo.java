package com.sofar.fun.ad;

import androidx.annotation.NonNull;

public class MockAdInfo implements AdInfo {

  String codeId;
  String provider;

  public MockAdInfo(String codeId, String provider) {
    this.codeId = codeId;
    this.provider = provider;
  }

  @NonNull
  @Override
  public String adCodeId() {
    return codeId;
  }

  @NonNull
  @Override
  public String adProvider() {
    return provider;
  }
}
