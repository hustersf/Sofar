package com.sofar.fun.play.core;

import android.view.View;

import com.sofar.base.viewbinder.RecyclerViewBinder;

public class PlayableRecyclerViewBinder extends RecyclerViewBinder implements Playable {

  protected PlayableViewBinder playViewBinder;

  public void setPlayViewBinder(PlayableViewBinder playViewBinder) {
    this.playViewBinder = playViewBinder;
    addViewBinder(playViewBinder);
  }

  @Override
  public void start() {
    if (playViewBinder != null) {
      playViewBinder.start();
    }
  }

  @Override
  public void stop() {
    if (playViewBinder != null) {
      playViewBinder.stop();
    }
  }

  @Override
  public boolean canPlay() {
    return playViewBinder != null && playViewBinder.canPlay();
  }

  @Override
  public float getViewShowRatio() {
    return playViewBinder != null ? playViewBinder.getViewShowRatio() : 0;
  }

  public View getItemView() {
    return view;
  }
}
