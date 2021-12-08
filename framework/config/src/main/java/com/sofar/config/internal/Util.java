package com.sofar.config.internal;

public class Util {

  public static String getKey(String... key) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < key.length; i++) {
      if (key[i] == null) {
        continue;
      }
      sb.append(key[i]);
      if (i != key.length - 1) {
        sb.append("/");
      }
    }
    return sb.toString();
  }

}
