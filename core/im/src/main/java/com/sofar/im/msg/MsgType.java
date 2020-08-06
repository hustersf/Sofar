package com.sofar.im.msg;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface MsgType {
  int TEXT = 0;
  int IMAGE = 1;
  int AUDIO = 2;
  int VIDEO = 3;
  int EMOTION = 4;
  int FILE = 5;
}
