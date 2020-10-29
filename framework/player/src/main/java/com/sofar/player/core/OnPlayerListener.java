package com.sofar.player.core;

public interface OnPlayerListener {

  void onPlayerStart();

  void onPlayerResume();

  void onPlayerPause();

  void onPlayerStop();

  void onPlayerCompleted();

  void onPlayerError();
}
