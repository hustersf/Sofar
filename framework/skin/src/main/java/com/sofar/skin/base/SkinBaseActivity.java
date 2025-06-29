package com.sofar.skin.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sofar.skin.core.SkinCompatDelegate;

/**
 * 需要换肤的页面 可以继承 SkinBaseActivity
 * 当然也可以使用 SkinCompatDelegate 支持换肤
 */
public class SkinBaseActivity extends AppCompatActivity {

  private SkinCompatDelegate mSkinDelegate;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    getSkinDelegate().installViewFactory();
    super.onCreate(savedInstanceState);
    getSkinDelegate().onCreate(savedInstanceState);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    getSkinDelegate().onDestroy();
  }

  public SkinCompatDelegate getSkinDelegate() {
    if (mSkinDelegate == null) {
      mSkinDelegate = new SkinCompatDelegate(this);
    }
    return mSkinDelegate;
  }
}


