package com.sofar.business.github.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.sofar.business.github.model.Repo;

@Database(version = 1, entities = {Repo.class},exportSchema = false)
public abstract class RepoDatabase extends RoomDatabase {

  private volatile static RepoDatabase instance;
  private static final String DB_NAME = "github.db";

  public abstract RepoDao getRepoDao();

  public static RepoDatabase getInstance(@NonNull Context context) {
    if (instance == null) {
      synchronized (RepoDatabase.class) {
        if (instance == null) {
          instance = buildDatabase(context);
        }
      }
    }
    return instance;
  }

  private static RepoDatabase buildDatabase(Context context) {
    return Room.databaseBuilder(context.getApplicationContext(), RepoDatabase.class, DB_NAME)
      .build();
  }

}
