package com.sofar.fun.badge;

import androidx.annotation.NonNull;

import com.sofar.fun.badge.db.BadgeRecordManager;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 获取树中 各个节点的 BadgeNumber信息
 */
public class BadgeNumberTreeManager {

  private static final String TAG = "BadgeNumberTreeManager";

  private BadgeNumberCache cache = new BadgeNumberCache();

  private BadgeNumberTreeManager() {
  }

  private static class Inner {
    final static BadgeNumberTreeManager INSTANCE = new BadgeNumberTreeManager();
  }

  public static BadgeNumberTreeManager get() {
    return Inner.INSTANCE;
  }

  public Observable<BadgeNumber> setBadgeNumber(@NonNull BadgeNumber badgeNumber) {
    return Observable.fromCallable(() -> {
      BadgeRecordManager.get().setBadgeNumber(badgeNumber);
      cache.clearBadgeNumberFromCache(badgeNumber.type);
      return badgeNumber;
    }).subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread());
  }

  /**
   * 获取指定类型BadgeNumber
   */
  public Observable<Integer> getBadgeNumber(int type) {
    return Observable.fromCallable(() -> {
      int count = cache.getBadgeNumberFromCache(type);
      if (count <= 0) {
        count = BadgeRecordManager.get().getBadgeNumberCount(type);
        if (count > 0) {
          cache.saveBadgeNumberToCache(type, count);
        }
      }
      return count;
    }).subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread());
  }

  /**
   * 计算父节点的BadgeNumber信息，包含数量和显示方式
   * 优先计算数量，在计算红点
   *
   * @param intervalList 类型区间列表，对应树中的父节点，包含[1,n]个类型区间
   */
  public Observable<BadgeNumberCountResult> getTotalBadgeNumberOnParent(@NonNull List<BadgeNumberInterval> intervalList) {
    return Observable.fromCallable(() -> {
      BadgeNumberCountResult result = getTotalBadgeNumberOnParent(intervalList, BadgeNumber.DISPLAY_MODE_ON_PARENT_NUMBER);
      if (result.totalCount <= 0) {
        result = getTotalBadgeNumberOnParent(intervalList, BadgeNumber.DISPLAY_MODE_ON_PARENT_DOT);
      }
      return result;
    }).subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread());
  }

  @NonNull
  private BadgeNumberCountResult getTotalBadgeNumberOnParent(@NonNull List<BadgeNumberInterval> intervalList, int displayMode) {
    BadgeNumberCountResult result = new BadgeNumberCountResult();
    int sumCount = 0;
    for (BadgeNumberInterval interval : intervalList) {
      sumCount += getBadgeNumber(interval.typeMin, interval.typeMax, displayMode);
    }
    result.totalCount = sumCount;
    result.displayMode = displayMode;
    return result;
  }

  private int getBadgeNumber(int typeMin, int typeMax, int displayMode) {
    int count = cache.getBadgeNumberFromCache(typeMin, typeMax, displayMode);
    if (count <= 0) {
      count = BadgeRecordManager.get().getBadgeNumberCount(typeMin, typeMax, displayMode);
      if (count > 0) {
        cache.saveBadgeNumberToCache(typeMin, typeMax, displayMode, count);
      }
    }
    return count;
  }

  /**
   * 删除指定类型BadgeNumber
   */
  public Observable<Boolean> clearBadgeNumber(int type) {
    return Observable.fromCallable(() -> {
      BadgeRecordManager.get().deleteBadgeNumber(type);
      cache.clearBadgeNumberFromCache(type);
      return true;
    }).subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread());
  }

}
