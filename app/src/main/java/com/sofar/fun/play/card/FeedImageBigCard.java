package com.sofar.fun.play.card;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.sofar.R;
import com.sofar.base.viewbinder.RecyclerViewBinder;
import com.sofar.fun.play.viewbinder.FeedCoverViewBinder;
import com.sofar.utility.ViewUtil;

public class FeedImageBigCard extends FeedItemCard {

  @NonNull
  @Override
  public View createView(ViewGroup parent) {
    return ViewUtil.inflate(parent, R.layout.feed_image_big_card_item);
  }

  @NonNull
  @Override
  public RecyclerViewBinder createViewBinder() {
    RecyclerViewBinder viewBinder = new RecyclerViewBinder();
    viewBinder.addViewBinder(new FeedCoverViewBinder());
    return viewBinder;
  }
}
