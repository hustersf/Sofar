package com.sofar.skin.util;

import java.util.Collection;

public class SkinCollectionUtil {

  public static <T> boolean isEmpty(Collection<T> list) {
    return list == null || list.isEmpty();
  }
}
