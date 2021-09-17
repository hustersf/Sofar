package com.sofar.fun.play.card;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.sofar.R;
import com.sofar.base.viewbinder.RecyclerViewBinder;
import com.sofar.fun.play.core.PlayableRecyclerViewBinder;
import com.sofar.fun.play.viewbinder.FeedVideoCoreViewBinder;
import com.sofar.fun.play.viewbinder.FeedVideoPlayPanelViewBinder;
import com.sofar.fun.play.viewbinder.FeedVideoPlayableViewBinder;
import com.sofar.fun.play.viewbinder.FeedVideoSizeViewBinder;
import com.sofar.utility.ViewUtil;

public class FeedPgcBigCard extends FeedPlayableCard {

  @NonNull
  @Override
  public View createView(ViewGroup parent) {
    return ViewUtil.inflate(parent, R.layout.feed_pgc_big_card_item);
  }

  @NonNull
  @Override
  public RecyclerViewBinder createViewBinder() {
    PlayableRecyclerViewBinder viewBinder = new PlayableRecyclerViewBinder();
    viewBinder.setPlayViewBinder(new FeedVideoPlayableViewBinder(mFeedPlayer));
    viewBinder.addViewBinder(new FeedVideoSizeViewBinder());
    viewBinder.addViewBinder(new FeedVideoCoreViewBinder());
    viewBinder.addViewBinder(new FeedVideoPlayPanelViewBinder());
    return viewBinder;
  }

}
