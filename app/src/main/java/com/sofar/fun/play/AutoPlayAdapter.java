package com.sofar.fun.play;

import java.util.Map;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;

import com.sofar.base.recycler.RecyclerAdapter;
import com.sofar.base.viewbinder.RecyclerViewBinder;
import com.sofar.fun.play.card.FeedItemCard;

public class AutoPlayAdapter extends RecyclerAdapter<Feed> {

  @NonNull
  private final Map<FeedViewType, FeedItemCard> cards;
  private final SparseArray<FeedItemCard> viewTypeToCards = new SparseArray<>();

  public AutoPlayAdapter(@NonNull Map<FeedViewType, FeedItemCard> map) {
    this.cards = map;
    for (Map.Entry<FeedViewType, FeedItemCard> entry : cards.entrySet()) {
      viewTypeToCards.put(entry.getKey().ordinal(), entry.getValue());
    }
  }

  @NonNull
  @Override
  protected View onCreateView(ViewGroup parent, int viewType) {
    return getFeedItemCard(viewType).createView(parent);
  }

  @NonNull
  @Override
  protected RecyclerViewBinder onCreateViewBinder(int viewType) {
    return getFeedItemCard(viewType).createViewBinder();
  }

  @Override
  protected Object getCallerContext(int viewType) {
    return getFeedItemCard(viewType).getCallerContext();
  }

  @Override
  public int getItemViewType(int position) {
    Feed feed = getItem(position);
    FeedViewType viewType = FeedViewType.getFeedType(feed);
    return viewType.ordinal();
  }

  @NonNull
  private FeedItemCard getFeedItemCard(int viewType) {
    if (viewTypeToCards.get(viewType) != null) {
      return viewTypeToCards.get(viewType);
    } else {
      return FeedItemCard.DEFAULT;
    }
  }

}
