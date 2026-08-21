package com.sofar.business.github.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sofar.business.github.model.Repo

@Database(entities = [Repo::class, RepoRemoteKeys::class], version = 1, exportSchema = false)
abstract class RepoDatabase : RoomDatabase() {
  abstract fun reposDao(): RepoDao
  abstract fun remoteKeysDao(): RemoteKeysDao

  companion object {
    @Volatile
    private var INSTANCE: RepoDatabase? = null
    fun getInstance(context: Context): RepoDatabase =
      INSTANCE ?: synchronized(this) {
        INSTANCE ?: Room.databaseBuilder(
          context.getApplicationContext(),
          RepoDatabase::class.java,
          "github.db"
        ).build().also { INSTANCE = it }
      }
  }
}
