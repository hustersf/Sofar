package com.sofar.config.internal;

import java.util.HashSet;

/**
 * value is either a String, a Java primitive, or a Java primitive wrapper type.
 */
public class Primitives {

  static HashSet<Class<?>> allTypes = new HashSet<>();

  static {
    addPrimitive(boolean.class, Boolean.class);
    addPrimitive(byte.class, Byte.class);
    addPrimitive(char.class, Character.class);
    addPrimitive(short.class, Short.class);
    addPrimitive(float.class, Float.class);
    addPrimitive(double.class, Double.class);
    addPrimitive(int.class, Integer.class);
    addPrimitive(long.class, Long.class);
    allTypes.add(String.class);
  }

  public static boolean isConfigPrimitive(Class<?> cls) {
    return allTypes.contains(cls);
  }

  private static void addPrimitive(Class<?> cls, Class<?> clsWrapper) {
    allTypes.add(cls);
    allTypes.add(clsWrapper);
  }

}
