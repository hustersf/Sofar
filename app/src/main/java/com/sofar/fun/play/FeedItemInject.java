package com.sofar.fun.play;

import java.util.Map;

import androidx.annotation.NonNull;

import com.sofar.fun.play.card.FeedItemCard;
import com.sofar.fun.play.card.FeedPlayableCard;
import com.sofar.fun.play.core.FeedPlayer;
import com.sofar.fun.play.core.RecyclerViewPlayer;

public class FeedItemInject {

  @NonNull
  private final Map<FeedViewType, FeedItemCard> map;

  private FeedPlayer mFeedPlayer;
  private RecyclerViewPlayer mRecyclerViewPlayer;

  public void setFeedPlayer(FeedPlayer feedPlayer) {
    mFeedPlayer = feedPlayer;
  }

  public void setRecyclerViewPlayer(RecyclerViewPlayer recyclerViewPlayer) {
    mRecyclerViewPlayer = recyclerViewPlayer;
  }

  public FeedItemInject(@NonNull Map<FeedViewType, FeedItemCard> map) {
    this.map = map;
  }

  public void bind() {
    for (FeedItemCard card : map.values()) {
      if (card instanceof FeedPlayableCard) {
        ((FeedPlayableCard) card).setFeedPlayer(mFeedPlayer);
        ((FeedPlayableCard) card).setRecyclerViewPlayer(mRecyclerViewPlayer);
      }
    }
  }

}
