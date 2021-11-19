package com.sofar.fun.play.card;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.sofar.R;
import com.sofar.base.viewbinder.RecyclerViewBinder;
import com.sofar.fun.play.core.PlayableRecyclerViewBinder;
import com.sofar.fun.play.viewbinder.FeedThreeImagePlayableViewBinder;
import com.sofar.utility.ViewUtil;

public class FeedImageThreePlayCard extends FeedPlayableCard {

  @NonNull
  @Override
  public View createView(ViewGroup parent) {
    return ViewUtil.inflate(parent, R.layout.feed_image_three_card_item);
  }

  @NonNull
  @Override
  public RecyclerViewBinder createViewBinder() {
    PlayableRecyclerViewBinder viewBinder = new PlayableRecyclerViewBinder();
    viewBinder.setPlayViewBinder(new FeedThreeImagePlayableViewBinder(mFeedPlayer, viewBinder));
    return viewBinder;
  }
}
