package com.sofar.fun.play.core;

public interface Playable {

  void start();

  void stop();

  default boolean canPlay() {
    return true;
  }

  default float getViewShowRatio() {
    return 0;
  }

}
