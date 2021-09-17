package com.sofar.base.viewbinder;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import java.util.ArrayList;
import java.util.List;

public class ViewBinder<T> {

  @NonNull
  public View view;
  public Context context;

  protected List<ViewBinder> viewBinders = new ArrayList<>();

  @UiThread
  public final void create(@NonNull View view) {
    this.view = view;
    context = view.getContext();
    onCreate();
    for (ViewBinder viewBinder : viewBinders) {
      viewBinder.create(view);
    }
  }

  public final <T extends View> T bindView(@IdRes int id) {
    return view.findViewById(id);
  }

  public final void bind(T data) {
    onBind(data);
    for (ViewBinder viewBinder : viewBinders) {
      viewBinder.bind(data);
    }
  }

  public final void bindExtra(Object extra) {
    onBindExtra(extra);
    for (ViewBinder viewBinder : viewBinders) {
      viewBinder.bindExtra(extra);
    }
  }

  public final void unbind() {
    onUnbind();
    for (ViewBinder viewBinder : viewBinders) {
      viewBinder.unbind();
    }
  }


  public final void destroy() {
    onDestroy();
    for (ViewBinder viewBinder : viewBinders) {
      viewBinder.destroy();
    }
  }

  public void addViewBinder(ViewBinder viewBinder) {
    viewBinders.add(viewBinder);
  }

  protected void onCreate() {

  }

  protected void onBind(T data) {

  }

  protected void onBindExtra(Object extra) {

  }

  protected void onUnbind() {

  }


  protected void onDestroy() {

  }

  @Nullable
  public Activity getActivity() {
    if (context instanceof Activity) {
      return ((Activity) context);
    }
    return null;
  }

}
