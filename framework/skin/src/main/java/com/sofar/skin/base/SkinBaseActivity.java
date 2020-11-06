package com.sofar.skin.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.LayoutInflaterCompat;

import com.sofar.skin.callback.IDynamicNewView;
import com.sofar.skin.callback.ISkinUpdate;
import com.sofar.skin.core.SkinInflaterFactory;
import com.sofar.skin.core.SkinManager;
import com.sofar.skin.model.DynamicAttr;

import java.util.List;

public class SkinBaseActivity extends AppCompatActivity implements ISkinUpdate, IDynamicNewView {

  private SkinInflaterFactory mSkinInflaterFactory;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    mSkinInflaterFactory = new SkinInflaterFactory(this);
    LayoutInflaterCompat.setFactory2(LayoutInflater.from(this), mSkinInflaterFactory);

    super.onCreate(savedInstanceState);
    SkinManager.getInstance().attach(this);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    SkinManager.getInstance().detach(this);
    if (mSkinInflaterFactory != null) {
      mSkinInflaterFactory.clean();
    }
  }

  @Override
  public void onThemeUpdate() {
    if (mSkinInflaterFactory != null) {
      mSkinInflaterFactory.applySkin();
    }
  }

  @Override
  public void dynamicAddView(View view, List<DynamicAttr> dynamicAttrs) {
    if (mSkinInflaterFactory != null) {
      mSkinInflaterFactory.dynamicAddSkinEnableView(this, view, dynamicAttrs);
    }
  }

  @Override
  public void dynamicAddView(View view, String attrName, int attrValueResId) {
    if (mSkinInflaterFactory != null) {
      mSkinInflaterFactory.dynamicAddSkinEnableView(this, view, attrName, attrValueResId);
    }
  }
}


