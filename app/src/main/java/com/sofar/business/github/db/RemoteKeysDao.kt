package com.sofar.business.github.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeysDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(remoteKey: List<RepoRemoteKeys>)

  @Query("SELECT * FROM remote_keys WHERE `query` = :query AND repoId = :repoId")
  suspend fun remoteKeys(query: String, repoId: Long): RepoRemoteKeys?

  @Query("DELETE FROM remote_keys WHERE `query` = :query")
  suspend fun clearRemoteKeys(query: String)
}