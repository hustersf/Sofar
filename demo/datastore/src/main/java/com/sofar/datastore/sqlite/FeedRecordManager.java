package com.sofar.datastore.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * sqlite 数据库使用示例
 */
public class FeedRecordManager {

  private SQLiteDatabase mSQLiteDatabase;
  private String tabName;

  private FeedRecordManager() {
    init();
  }

  private static class Inner {
    private static FeedRecordManager INSTANCE = new FeedRecordManager();
  }

  public FeedRecordManager get() {
    return Inner.INSTANCE;
  }

  private void init() {
    // 打开数据库
    mSQLiteDatabase = new FeedSQLiteDBHelper(null).getWritableDatabase();
  }

  public void insert() {
    //方案1: SQLiteDatabase 封装的 插入方法
    ContentValues contentValues = new ContentValues();
    contentValues.put("key", "value");
    mSQLiteDatabase.insert(tabName, null, contentValues);

    //方案2: 自己写sql语句
    mSQLiteDatabase.execSQL("");
  }

  public void delete() {
    //方案1: SQLiteDatabase 封装的 删除方法
    //提供条件语句即可
    mSQLiteDatabase.delete(tabName, "id=?", new String[]{"11111"});

    //方案2: 自己写sql语句
    mSQLiteDatabase.execSQL("");
  }

  public void update() {
    //方案1: SQLiteDatabase 封装的 更新方法
    ContentValues contentValues = new ContentValues();
    contentValues.put("key", "value");
    mSQLiteDatabase.update(tabName, contentValues, "id=?", new String[]{"11111"});

    //方案2: 自己写sql语句
    mSQLiteDatabase.execSQL("");
  }

  public void query() {
    //方案1: SQLiteDatabase 封装的 查询方法
    //省略参数, 熟悉 sql 语句 就能明白参数的含义
    Cursor cursor = mSQLiteDatabase.query(tabName, new String[]{"name", "sex", "age"}, "name=?",
      new String[]{"隔壁老王"}, null, null, null);

    //方案2: 自己写sql语句
    Cursor rawQuery =
      mSQLiteDatabase.rawQuery("select * from " + tabName + " where name=?", new String[]{"哈利波特"});

    //拿到 cursor 之后可以遍历拿到数据
  }
}
