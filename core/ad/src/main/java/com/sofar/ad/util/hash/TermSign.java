package com.sofar.ad.util.hash;

/**
 * TERM SIGN 实现类
 */
// CHECKSTYLE:OFF
public class TermSign {

  public static long calcTermSign(byte[] bytes) {
    long sign = 0;
    if (bytes.length <= 8) {
      int i = 0;
      while (i < bytes.length) {
        sign |= (long) bytes[i] << (i << 3);
        ++i;
      }
    } else {
      sign = CityHash.cityHash128(bytes, 0, bytes.length)[0];
    }
    return sign;
  }
}
