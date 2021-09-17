package com.sofar.fun.play.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FeedPlayerInternal {

  private static final String TAG = "FeedPlayerInternal";

  private Queue<Playable> mPlayableQueue = new LinkedList<>();
  @NonNull
  private FeedPlayer mPlayer;
  @Nullable
  private Playable mCurrentPlay;

  FeedPlayerInternal(@NonNull FeedPlayer player) {
    this.mPlayer = player;
  }

  void play(List<Playable> list) {
    if (list != null && !list.isEmpty()) {
      internalPlay(list);
      mPlayer.onStart();
      Log.d(TAG, "play start");
    }
  }

  void playNext(Playable last) {
    Log.d(TAG, "play next");
    mPlayer.assetsPlayed(last);
    internalSchedule();
  }

  void stop() {
    internalStop();
    mPlayer.onStop();
    Log.d(TAG, "play stop");
  }

  private void internalPlay(@NonNull List<Playable> list) {
    mPlayableQueue.clear();
    mPlayableQueue.addAll(list);
    internalSchedule();
  }

  private void internalSchedule() {
    Playable playable = mPlayableQueue.poll();
    if (mCurrentPlay == playable) {
      Log.d(TAG, "internalSchedule mCurrentPlay == playable");
      return;
    }

    stopCurrent();
    if (playable != null) {
      mCurrentPlay = playable;
      playable.start();
    } else {
      mPlayer.allAssetsPlayed();
      Log.d(TAG, "player all finished");
    }
  }

  private void stopCurrent() {
    if (mCurrentPlay != null) {
      mCurrentPlay.stop();
    }
    mCurrentPlay = null;
  }

  private void internalStop() {
    stopCurrent();
    mPlayableQueue.clear();
  }

  void release() {

  }

  Playable getPlaying() {
    return mCurrentPlay;
  }

  boolean isPlaying() {
    return mCurrentPlay != null;
  }

}
