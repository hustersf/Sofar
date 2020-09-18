package com.sofar.base.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

public class AppLifeObserver implements Application.ActivityLifecycleCallbacks, LifecycleEventObserver {

  @Override
  public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
    AppLifeManager.get().addActivity(activity);
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
    AppLifeManager.get().removeActivity(activity);
  }

  @Override
  public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
    if (event == Lifecycle.Event.ON_START) {
      AppLifeManager.get().onForeground();
    } else if (event == Lifecycle.Event.ON_STOP) {
      AppLifeManager.get().onBackground();
    }
  }
}
