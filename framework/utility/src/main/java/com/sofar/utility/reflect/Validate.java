package com.sofar.utility.reflect;

class Validate {

  static void isTrue(final boolean expression, final String message, final Object... values) {
    if (!expression) {
      throw new IllegalArgumentException(String.format(message, values));
    }
  }
}
