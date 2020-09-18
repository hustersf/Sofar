package com.sofar.base.app;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ProcessLifecycleOwner;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AppLifeManager {
  private static final String TAG = "AppLifeManager";

  boolean appOnForeground;

  List<WeakReference<Activity>> pages = new ArrayList<>();

  private AppLifeManager() {
  }

  private static class Inner {
    static AppLifeManager INSTANCE = new AppLifeManager();
  }

  public static AppLifeManager get() {
    return Inner.INSTANCE;
  }

  public void init(Application app) {
    AppLifeObserver observer = new AppLifeObserver();
    app.registerActivityLifecycleCallbacks(observer);
    ProcessLifecycleOwner.get().getLifecycle().addObserver(observer);
  }

  /**
   * @return 判断app是否在前台
   */
  public boolean isAppOnForeground() {
    return appOnForeground;
  }

  /**
   * @return 获取栈顶Activity
   */
  @Nullable
  public Activity getCurrentActivity() {
    synchronized (pages) {
      WeakReference<Activity> wrActivity = pages.get(pages.size() - 1);
      if (wrActivity != null) {
        return wrActivity.get();
      }
    }
    return null;
  }

  void addActivity(@NonNull Activity activity) {
    synchronized (pages) {
      pages.add(new WeakReference<>(activity));
    }
  }

  void removeActivity(@NonNull Activity activity) {
    synchronized (pages) {
      for (Iterator<WeakReference<Activity>> iterator = pages.iterator(); iterator.hasNext(); ) {
        WeakReference<Activity> wrActivity = iterator.next();
        if (wrActivity == null || wrActivity.get() == null || activity == wrActivity.get()) {
          iterator.remove();
        }
      }
    }
  }

  /**
   * app切换到前台
   */
  void onForeground() {
    appOnForeground = true;
    //todo 发射EventBus or 设置监听
    Log.d(TAG, "onForeground");
  }

  /**
   * app切换到回台
   */
  void onBackground() {
    appOnForeground = false;
    //todo 发射EventBus or 设置监听
    Log.d(TAG, "onBackground");
  }

}
