package com.sofar.fun.play.card;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.sofar.R;
import com.sofar.base.viewbinder.RecyclerViewBinder;
import com.sofar.utility.ViewUtil;

public abstract class FeedItemCard {

  @NonNull
  public abstract View createView(ViewGroup parent);

  @NonNull
  public abstract RecyclerViewBinder createViewBinder();

  @NonNull
  public Object getCallerContext() {
    return new Object();
  }

  public static FeedItemCard DEFAULT = new FeedItemCard() {

    @NonNull
    @Override
    public View createView(ViewGroup parent) {
      return ViewUtil.inflate(parent, R.layout.feed_item_unsupport);
    }

    @NonNull
    @Override
    public RecyclerViewBinder createViewBinder() {
      RecyclerViewBinder viewBinder = new RecyclerViewBinder();
      return viewBinder;
    }
  };

}
