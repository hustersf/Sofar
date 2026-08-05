package com.sofar.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sofar.room.model.UserEntity

@Dao
interface UserDao {
  @Insert
  fun insert(user: UserEntity): Long   //返回新条目的rowId

  @Update
  fun update(user: UserEntity): Int  //返回受影响的行数

  @Delete
  fun delete(user: UserEntity): Int //返回受影响的行数

  @Query("SELECT * FROM users")
  fun getAllUsers(): List<UserEntity>

  @Query(value = "SELECT * FROM users WHERE id = :userId")
  fun getUserById(userId: Int): UserEntity?

  @Query("SELECT * FROM users WHERE name LIKE :name")
  fun findByName(name: String): List<UserEntity>
}