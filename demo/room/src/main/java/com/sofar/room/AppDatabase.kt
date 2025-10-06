package com.sofar.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sofar.room.dao.UserDao
import com.sofar.room.model.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
  abstract fun userDao(): UserDao

  companion object {
    @Volatile
    private var instance: AppDatabase? = null

    fun get(context: Context): AppDatabase =
      instance ?: synchronized(this) {
        instance ?: Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "my_database.db"
        ).build() // 可以在此添加更多配置，例如 .addMigrations() 或 .addCallback()
          .also {
            instance = it
          }
      }
  }
}