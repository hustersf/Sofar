package com.sofar.player.core;

public interface OnPlayerListener {

  default void onPlayerStart() {}

  default void onPlayerResume() {}

  default void onPlayerPause() {}

  default void onPlayerStop() {}

  default void onPlayerCompleted() {}

  default void onPlayerError() {}
}
