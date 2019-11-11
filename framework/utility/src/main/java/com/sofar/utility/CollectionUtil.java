package com.sofar.utility;

import java.util.Collection;
import java.util.Map;

public class CollectionUtil {

  /**
   * list判空
   */
  public static <T> boolean isEmpty(Collection<T> list) {
    return list == null || list.isEmpty();
  }

  /**
   * map判空
   */
  public static <K, V> boolean isEmpty(Map<K, V> map) {
    return map == null || map.isEmpty();
  }
}
