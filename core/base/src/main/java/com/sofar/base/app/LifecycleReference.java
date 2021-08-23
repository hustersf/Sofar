package com.sofar.base.app;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;

/**
 * Activity/Fragment 作用域内创建一个唯一的对象
 * 可用作Activity/Fragment 数据共享
 */
public class LifecycleReference {

  private static HashMap<Object, LifecycleReference> referenceMap = new HashMap<>();

  public static LifecycleReference of(@NonNull AppCompatActivity activity) {
    if (referenceMap.get(activity) == null) {
      referenceMap.put(activity, new LifecycleReference(activity));
    }
    return referenceMap.get(activity);
  }


  public static LifecycleReference of(@NonNull Fragment fragment) {
    if (referenceMap.get(fragment) == null) {
      referenceMap.put(fragment, new LifecycleReference(fragment));
    }
    return referenceMap.get(fragment);
  }

  private LifecycleReference(@NonNull AppCompatActivity activity) {
    activity.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
      if (event == Lifecycle.Event.ON_DESTROY) {
        referenceMap.remove(activity);
        map.clear();
      }
    });
  }

  private LifecycleReference(@NonNull Fragment fragment) {
    fragment.getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
      if (event == Lifecycle.Event.ON_DESTROY) {
        referenceMap.remove(fragment);
        map.clear();
      }
    });
  }

  private HashMap<String, Object> map = new HashMap<>();

  public <T> T provide(Class<T> modelClass, Factory<T> factory) {
    String key = "LifecycleReference." + modelClass.getCanonicalName();
    if (map.get(key) == null) {
      map.put(key, factory.create());
    }
    return (T) map.get(key);
  }

  public interface Factory<T> {
    T create();
  }
}
