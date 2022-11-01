package com.sofar.datastore.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class FeedSQLiteDBHelper extends SQLiteOpenHelper {

  private static final int DB_VERSION = 1;
  private static final String DB_NAME = "feed_record.db";
  private static final String TABLE_NAME = "feed";

  public FeedSQLiteDBHelper(@Nullable Context context) {
    super(context, DB_NAME, null, DB_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    // 创建 数据库中的 表, 自己写 sql 语句
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // 数据库升级操作,两种方案

    //1.旧数据迁移到新数据
    //2.删除旧表, 重新创建新的表
  }
}
