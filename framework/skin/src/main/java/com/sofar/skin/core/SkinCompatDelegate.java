package com.sofar.skin.core;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.LayoutInflaterCompat;

import com.sofar.skin.callback.IDynamicNewView;
import com.sofar.skin.callback.ISkinUpdate;
import com.sofar.skin.model.DynamicAttr;

/**
 * {@link AppCompatActivity}
 * 代理 Activity 的行为,处理换肤相关逻辑
 */
public class SkinCompatDelegate implements LayoutInflater.Factory2, ISkinUpdate, IDynamicNewView {

  private final AppCompatActivity mActivity;
  private SkinCompatViewInflater mSkinCompatViewInflater;

  public SkinCompatDelegate(@NonNull AppCompatActivity activity) {
    mActivity = activity;
  }

  /**
   * 替换 Factory2, 尽量在 super.onCreate 之前调用
   */
  public void installViewFactory() {
    LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
    LayoutInflaterCompat.setFactory2(layoutInflater, this);
  }

  public void onCreate(Bundle savedInstanceState) {
    SkinObserverManager.get().attach(this);
  }

  public void onDestroy() {
    SkinObserverManager.get().detach(this);
    getSkinCompatViewInflater().clean();
  }

  public Context getContext() {
    return mActivity;
  }

  @Nullable
  @Override
  public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context,
    @NonNull AttributeSet attrs) {
    return createView(parent, name, context, attrs);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull String name, @NonNull Context context,
    @NonNull AttributeSet attrs) {
    return createView(null, name, context, attrs);
  }

  public View createView(@Nullable View parent, final String name, @NonNull Context context,
    @NonNull AttributeSet attrs) {
    AppCompatDelegate delegate = mActivity.getDelegate();
    return getSkinCompatViewInflater().createView(delegate, parent, name, context, attrs);
  }

  @Override
  public void onThemeUpdate() {
    getSkinCompatViewInflater().applySkin();
  }

  @Override
  public void dynamicAddView(@NonNull View view, List<DynamicAttr> dynamicAttrs) {
    getSkinCompatViewInflater().dynamicAddSkinView(view, dynamicAttrs);
  }

  @Override
  public void dynamicAddView(@NonNull View view, String attrName, int attrValueResId) {
    getSkinCompatViewInflater().dynamicAddSkinView(view, attrName, attrValueResId);
  }

  @NonNull
  public SkinCompatViewInflater getSkinCompatViewInflater() {
    if (mSkinCompatViewInflater == null) {
      mSkinCompatViewInflater = new SkinCompatViewInflater();
    }
    return mSkinCompatViewInflater;
  }
}
