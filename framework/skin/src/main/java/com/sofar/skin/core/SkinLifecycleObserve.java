package com.sofar.skin.core;

import java.util.WeakHashMap;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 监听全局 Activity 变化, 插入换肤功能
 */
public class SkinLifecycleObserve implements Application.ActivityLifecycleCallbacks {

  private WeakHashMap<Activity, SkinCompatDelegate> map = new WeakHashMap<>();

  @Override
  public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
    if (activity instanceof AppCompatActivity && !map.containsKey(activity)) {
      SkinCompatDelegate delegate = new SkinCompatDelegate((AppCompatActivity) activity);
      delegate.installViewFactory();
      delegate.onCreate(savedInstanceState);
      map.put(activity, delegate);
    }
  }

  @Override
  public void onActivityStarted(@NonNull Activity activity) {

  }

  @Override
  public void onActivityResumed(@NonNull Activity activity) {

  }

  @Override
  public void onActivityPaused(@NonNull Activity activity) {

  }

  @Override
  public void onActivityStopped(@NonNull Activity activity) {

  }

  @Override
  public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

  }

  @Override
  public void onActivityDestroyed(@NonNull Activity activity) {
    if (map.containsKey(activity)) {
      map.get(activity).onDestroy();
      map.remove(activity);
    }
  }
}
