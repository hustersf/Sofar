package com.sofar.base.viewbinder;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
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


  public final void bind(T data) {
    onBind(data);
    for (ViewBinder viewBinder : viewBinders) {
      viewBinder.bind(data);
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


  protected void onDestroy() {

  }

}
