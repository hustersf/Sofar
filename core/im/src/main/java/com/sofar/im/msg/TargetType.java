package com.sofar.im.msg;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface TargetType {
  int SINGLE = 0;  //单人
  int GROUP = 1;  //群
}
