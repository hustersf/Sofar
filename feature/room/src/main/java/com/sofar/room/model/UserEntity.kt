package com.sofar.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  @ColumnInfo(name = "name") val name: String?,
  @ColumnInfo(name = "age") val age: Int
)