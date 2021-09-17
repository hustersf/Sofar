package com.sofar.fun.play.core;

import java.util.List;

import androidx.annotation.Nullable;

public class FeedPlayer {

  private List<Playable> mPlayableList;
  private FeedPlayerInternal mInternal;
  private PlayCallback mPlayCallback;

  public static FeedPlayer create() {
    return new FeedPlayer();
  }

  private FeedPlayer() {
    mInternal = new FeedPlayerInternal(this);
  }

  public FeedPlayer feed(List<Playable> list) {
    mPlayableList = list;
    return this;
  }

  public FeedPlayer callback(PlayCallback callback) {
    mPlayCallback = callback;
    return this;
  }

  public void start() {
    mInternal.play(mPlayableList);
  }

  public void stop() {
    mInternal.stop();
  }

  public void playFinished(Playable playable) {
    mInternal.playNext(playable);
  }

  public void release() {
    mInternal.release();
  }

  @Nullable
  public Playable getPlaying() {
    return mInternal.getPlaying();
  }

  public boolean isPlaying() {
    return mInternal.isPlaying();
  }

  void assetsPlayed(Playable playable) {
    if (mPlayCallback != null) {
      mPlayCallback.onPlayFinished(playable);
    }
  }

  void allAssetsPlayed() {
    if (mPlayCallback != null) {
      mPlayCallback.onAllFinished();
    }
  }

  void onStart() {
    if (mPlayCallback != null) {
      mPlayCallback.onPlayStart();
    }
  }

  void onStop() {
    if (mPlayCallback != null) {
      mPlayCallback.onPlayStop();
    }
  }

}
