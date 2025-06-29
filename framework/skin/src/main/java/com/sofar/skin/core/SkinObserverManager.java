package com.sofar.skin.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.View;
import androidx.annotation.NonNull;

import com.sofar.skin.callback.IDynamicNewView;
import com.sofar.skin.callback.ISkinObserver;
import com.sofar.skin.callback.ISkinUpdate;
import com.sofar.skin.model.DynamicAttr;

/**
 * 管理皮肤刷新
 */
public class SkinObserverManager implements ISkinObserver, IDynamicNewView {

  private List<ISkinUpdate> skinObservers = new ArrayList<>();
  private HashMap<Context, SkinCompatDelegate> skinMap = new HashMap<>();

  private SkinObserverManager() {
  }

  @Override
  public void attach(ISkinUpdate observer) {
    if (!skinObservers.contains(observer)) {
      skinObservers.add(observer);
    }

    if (observer instanceof SkinCompatDelegate) {
      SkinCompatDelegate delegate = (SkinCompatDelegate) observer;
      skinMap.put(delegate.getContext(), delegate);
    }
  }

  @Override
  public void detach(ISkinUpdate observer) {
    if (skinObservers.contains(observer)) {
      skinObservers.remove(observer);
    }

    if (observer instanceof SkinCompatDelegate) {
      SkinCompatDelegate delegate = (SkinCompatDelegate) observer;
      skinMap.remove(delegate.getContext());
    }
  }

  @Override
  public void notifySkinUpdate() {
    for (ISkinUpdate observer : skinObservers) {
      observer.onThemeUpdate();
    }
  }

  @Override
  public void dynamicAddView(@NonNull View view, List<DynamicAttr> dynamicAttrs) {
    if (skinMap.containsKey(view.getContext())) {
      skinMap.get(view.getContext()).dynamicAddView(view, dynamicAttrs);
    }
  }

  @Override
  public void dynamicAddView(@NonNull View view, String attrName, int attrValueResId) {
    if (skinMap.containsKey(view.getContext())) {
      skinMap.get(view.getContext()).dynamicAddView(view, attrName, attrValueResId);
    }
  }

  private static class Inner {
    private static SkinObserverManager INSTANCE = new SkinObserverManager();
  }

  public static SkinObserverManager get() {
    return Inner.INSTANCE;
  }
}
