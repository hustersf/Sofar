package com.sofar.fun.badge.db;

import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import com.sofar.fun.FunConfig;
import com.sofar.fun.badge.BadgeNumber;

import java.util.ArrayList;
import java.util.List;

public class BadgeRecordManager {

  private DaoSession daoSession;

  private BadgeRecordManager() {
    init();
  }

  private static class Inner {
    final static BadgeRecordManager INSTANCE = new BadgeRecordManager();
  }

  public static BadgeRecordManager get() {
    return BadgeRecordManager.Inner.INSTANCE;
  }

  private void init() {
    String dbName = "badge_record.db";
    BadgeRecordDBOpenHelper helper =
      new BadgeRecordDBOpenHelper(FunConfig.theApp, dbName, null);
    SQLiteDatabase db = helper.getWritableDatabase();
    daoSession = new DaoMaster(db).newSession();
  }


  public void setBadgeNumber(@NonNull BadgeNumber badgeNumber) {
    BadgeRecord record = new BadgeRecord();
    record.setType(badgeNumber.type);
    record.setCount(badgeNumber.count);
    record.setDisplayMode(badgeNumber.displayMode);
    daoSession.getBadgeRecordDao().insertOrReplaceInTx(record);
  }


  public void addBadgeNumber(@NonNull BadgeNumber badgeNumber) {
    BadgeRecord record = daoSession.getBadgeRecordDao()
      .queryBuilder()
      .where(BadgeRecordDao.Properties.Type.eq(badgeNumber.type))
      .build().unique();
    int oldCount = 0;
    if (record != null) {
      oldCount = record.getCount();
    } else {
      record = new BadgeRecord();
    }
    record.setType(badgeNumber.type);
    record.setCount(badgeNumber.count + oldCount);
    record.setDisplayMode(badgeNumber.displayMode);
    daoSession.getBadgeRecordDao().insertOrReplaceInTx(record);
  }

  public List<BadgeNumber> queryAll() {
    List<BadgeRecord> records = daoSession.getBadgeRecordDao().loadAll();
    List<BadgeNumber> list = new ArrayList<>();
    for (BadgeRecord record : records) {
      BadgeNumber number = new BadgeNumber();
      number.type = record.getType();
      number.count = record.getCount();
      number.displayMode = record.getDisplayMode();
      list.add(number);
    }
    return list;
  }

  public void deleteBadgeNumber(int type) {
    BadgeRecord record = daoSession.getBadgeRecordDao()
      .queryBuilder()
      .where(BadgeRecordDao.Properties.Type.eq(type))
      .build().unique();
    if (record != null) {
      daoSession.getBadgeRecordDao().delete(record);
    }
  }

  public int getBadgeNumberCount(int type) {
    BadgeRecord record = daoSession.getBadgeRecordDao()
      .queryBuilder()
      .where(BadgeRecordDao.Properties.Type.eq(type))
      .build().unique();
    if (record != null) {
      return record.getCount();
    }
    return -1;
  }

  public int getBadgeNumberCount(int typeMin, int typeMax, int displayMode) {
    List<BadgeRecord> records = daoSession.getBadgeRecordDao()
      .queryBuilder()
      .where(BadgeRecordDao.Properties.Type.between(typeMin, typeMax), BadgeRecordDao.Properties.DisplayMode.eq(displayMode))
      .build().list();
    int sumCount = 0;
    for (BadgeRecord record : records) {
      sumCount += record.getCount();
    }
    return sumCount;
  }

}
