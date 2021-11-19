package com.sofar.fun.play.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sofar.base.recycler.RecyclerViewHolder;

public class RecyclerViewPlayer implements PlayCallback {

  private static final String TAG = "RecyclerViewPlayer";

  protected RecyclerView mRecyclerView;
  protected boolean enable = true;
  protected FeedPlayer mPlayer;

  protected int itemScroll = ItemScroll.UNKNOWN;

  RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
      super.onScrollStateChanged(recyclerView, newState);
      Log.d(TAG, "newState=" + newState);
      if (newState == RecyclerView.SCROLL_STATE_IDLE) {
        recapture();
      }
    }
  };

  public RecyclerViewPlayer(@NonNull FeedPlayer player) {
    mPlayer = player;
    mPlayer.callback(this);
  }

  public void attachToRecyclerView(@NonNull RecyclerView recyclerView) {
    if (mRecyclerView != null) {
      mRecyclerView.removeOnScrollListener(scrollListener);
    }

    mRecyclerView = recyclerView;
    mRecyclerView.addOnScrollListener(scrollListener);
  }

  public void setItemScroll(@ItemScroll int itemScroll) {
    this.itemScroll = itemScroll;
  }

  @NonNull
  protected List<Playable> findPlayableList() {
    List<Playable> list = new ArrayList<>();
    float maxRatio = 0;
    Playable maxRatioPlayable = null;
    for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
      View childView = mRecyclerView.getChildAt(i);
      final RecyclerView.ViewHolder vh = mRecyclerView.getChildViewHolder(childView);
      if (vh instanceof RecyclerViewHolder &&
        ((RecyclerViewHolder<?>) vh).viewBinder instanceof PlayableRecyclerViewBinder) {
        PlayableRecyclerViewBinder viewBinder =
          (PlayableRecyclerViewBinder) ((RecyclerViewHolder<?>) vh).viewBinder;
        int position = mRecyclerView.getChildAdapterPosition(childView);
        float ratio = viewBinder.getViewShowRatio();
        if (viewBinder.canPlay() && ratio > maxRatio) {
          maxRatio = ratio;
          maxRatioPlayable = viewBinder;
        }
      }
    }

    Log.d(TAG, "maxRatio=" + maxRatio);
    if (maxRatioPlayable != null) {
      list.add(maxRatioPlayable);
    }

    return list;
  }

  protected void maybePlay(@NonNull List<Playable> list) {
    Log.d(TAG, "play list count=" + list.size());
    if (list.isEmpty()) {
      mPlayer.feed(Collections.emptyList());
      mPlayer.stop();
    } else {
      mPlayer.feed(list).start();
    }
  }

  public void recapture() {
    if (enable) {
      List<Playable> list = findPlayableList();
      maybePlay(list);
    }
  }

  public void enable() {
    enable(true);
  }

  public void enable(boolean recapture) {
    enable = true;
    if (recapture) {
      recapture();
    }
  }

  public void disableNotStop() {
    enable = false;
  }

  public void disable() {
    enable = false;
    mPlayer.stop();
  }

  public boolean isEnable() {
    return enable;
  }


  public void release() {

  }

  @Override
  public void onPlayFinished(Playable playable) {
    Log.d(TAG, "onPlayFinished");
    scrollWhenPlayFinish(playable);
  }

  @Override
  public void onAllFinished() {
    Log.d(TAG, "onAllFinished");
  }

  @Override
  public void onPlayStart() {
    Log.d(TAG, "onPlayStart");
  }

  @Override
  public void onPlayStop() {
    Log.d(TAG, "onPlayStop");
  }

  private void scrollWhenPlayFinish(Playable playable) {
    Log.d(TAG, "item scroll=" + itemScroll);
    if (itemScroll == ItemScroll.UNKNOWN) {
      return;
    }

    if (!mRecyclerView.canScrollVertically(1)) {
      Log.d(TAG, "can not scroll");
      return;
    }

    if (playable instanceof PlayableRecyclerViewBinder &&
      ((PlayableRecyclerViewBinder) playable).getItemView() != null) {
      View itemView = ((PlayableRecyclerViewBinder) playable).getItemView();
      int dy = 0;
      if (itemView.getTop() < 0 || itemScroll == ItemScroll.TOP) {
        dy = itemView.getBottom();
      }
      if (itemScroll == ItemScroll.ONE_ITEM) {
        dy = itemView.getBottom() - itemView.getTop();
      }
      Log.d(TAG, "scroll dy=" + dy);
      mRecyclerView.smoothScrollBy(0, dy);
    } else {
      Log.d(TAG, "check playable is PlayableRecyclerViewBinder");
    }
  }

}
