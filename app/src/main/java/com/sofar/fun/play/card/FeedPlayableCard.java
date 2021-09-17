package com.sofar.fun.play.card;

import androidx.annotation.NonNull;

import com.sofar.fun.play.VideoContext;
import com.sofar.fun.play.core.FeedPlayer;
import com.sofar.fun.play.core.RecyclerViewPlayer;

public abstract class FeedPlayableCard extends FeedItemCard {

  protected FeedPlayer mFeedPlayer;
  protected RecyclerViewPlayer mRecyclerViewPlayer;

  public void setFeedPlayer(FeedPlayer feedPlayer) {
    mFeedPlayer = feedPlayer;
  }

  public void setRecyclerViewPlayer(RecyclerViewPlayer recyclerViewPlayer) {
    mRecyclerViewPlayer = recyclerViewPlayer;
  }

  @NonNull
  @Override
  public Object getCallerContext() {
    VideoContext videoContext = new VideoContext();
    return videoContext;
  }
}
