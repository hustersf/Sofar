package com.sofar.fun.play.singal;

public enum VideoControlSignal {
  START,
  RESUME,
  PAUSE,
  STOP,
  SEEK_TO_PERCENT,
  UPDATE_PROGRESS;

  private Object mTag;

  public VideoControlSignal setTag(Object tag) {
    mTag = tag;
    return this;
  }

  public Object getTag() {
    return mTag;
  }

  public void reset() {
    mTag = null;
  }

}
