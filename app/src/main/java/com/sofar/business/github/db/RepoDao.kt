package com.sofar.business.github.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sofar.business.github.model.Repo

@Dao
interface RepoDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(repos: List<Repo>)

  @Query(
    """
        SELECT repos.* FROM repos 
        INNER JOIN remote_keys ON repos.id = remote_keys.repoId 
        WHERE remote_keys.`query` = :query 
        ORDER BY remote_keys.position ASC
 """
  )
  fun reposByQuery(query: String): PagingSource<Int, Repo>

  @Query("DELETE FROM repos WHERE id IN (SELECT repoId FROM remote_keys WHERE `query` = :query)")
  suspend fun clearReposByQuery(query: String)
}
