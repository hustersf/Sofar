package com.sofar.fun.play.core;

public interface PlayCallback {

  default void onPlayFinished(Playable playable) {}

  default void onAllFinished() {}

  default void onPlayStart() {}

  default void onPlayStop() {}

}
