package com.sofar.im.link;

import com.sofar.utility.hash.TermSign;

import java.util.UUID;

public class Util {

  public static String randomId() {
    try {
      String uuId = UUID.randomUUID().toString();
      long sign = TermSign.calcTermSign(uuId.getBytes());
      return String.valueOf(sign);
    } catch (Exception e) {
      return String.valueOf(System.currentTimeMillis());
    }
  }
}
