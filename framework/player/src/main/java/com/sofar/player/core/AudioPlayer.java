package com.sofar.player.core;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * 音频播放
 */
public class AudioPlayer extends BasePlayer {

  public AudioPlayer(@NonNull Context context) {
    super(context);
    TAG = "AudioPlayer";
  }

  public AudioPlayer(@NonNull Context context, @NonNull String uri) {
    super(context, uri);
    TAG = "AudioPlayer";
  }

  @Override
  public void start() {
    super.start();
    if (playerListener != null) {
      playerListener.onPlayerStart();
    }
  }
}
