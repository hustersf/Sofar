package com.sofar.utility;

public class TimeUtil {

  public static String timeSpreadFormat(long time) {
    long h = time / (60 * 60 * 1000);
    long m = time / (60 * 1000) - h * 60;
    long s = time / 1000 - m * 60 - h * 3600;
    if (h > 0) {
      return String.format("%02d:%02d:%02d", h, m, s);
    } else {
      return String.format("%02d:%02d", m, s);
    }
  }

}
