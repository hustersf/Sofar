package com.sofar.fun.play;

public class AutoPlaySignal {

  @Command
  public String command;

  public int playPosition;
  @PlayStatus
  public String playStatus;

  public @interface PlayStatus {
    String PLAY_FINISH = "play_finish";
  }

  public @interface Command {
    String PLAY_STATUS = "play_status";
    String PLAY_POSITION = "play_position";
  }
}
