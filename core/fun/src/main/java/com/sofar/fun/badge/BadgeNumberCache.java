package com.sofar.fun.badge;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 红点信息缓存
 */
public class BadgeNumberCache {

  List<Entry> entries = new LinkedList<>();

  private static final int NO_DISPLAY_MODE = -1;


  /**
   * 从cache里取badge number的count值([typeMin,typeMax]总个数)
   *
   * @return 返回-1表示没有命中。
   */
  public synchronized int getBadgeNumberFromCache(int typeMin, int typeMax, int displayMode) {
    for (Entry cacheEntry : entries) {
      if (typeMin == cacheEntry.typeMin
        && typeMax == cacheEntry.typeMax
        && displayMode == cacheEntry.displayMode) {
        return cacheEntry.count;
      }
    }
    return -1;
  }

  public int getBadgeNumberFromCache(int type) {
    return getBadgeNumberFromCache(type, type, NO_DISPLAY_MODE);
  }

  public synchronized void saveBadgeNumberToCache(int typeMin, int typeMax, int displayMode, int count) {
    Entry cacheEntry = new Entry();
    cacheEntry.typeMin = typeMin;
    cacheEntry.typeMax = typeMax;
    cacheEntry.displayMode = displayMode;
    cacheEntry.count = count;
    entries.add(cacheEntry);
  }

  public void saveBadgeNumberToCache(int type, int count) {
    saveBadgeNumberToCache(type, type, NO_DISPLAY_MODE, count);
  }

  public synchronized void clearBadgeNumberFromCache(int type) {
    for (Iterator<Entry> iterator = entries.iterator(); iterator.hasNext(); ) {
      Entry cacheEntry = iterator.next();
      if (type >= cacheEntry.typeMin && type <= cacheEntry.typeMax) {
        //包含指定类型的区间, 其缓存的值全部清除.
        iterator.remove();
      }
    }
  }

  /**
   * 存储[typeMin,typeMax]区间红点数量，存在typeMin=typeMax
   */
  public static class Entry {
    public int typeMin;
    public int typeMax;
    public int count; //缓存的badge number总数
    public int displayMode;
  }
}
