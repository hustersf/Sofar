package com.sofar.business.github.db

import androidx.room.Entity

@Entity(tableName = "remote_keys", primaryKeys = ["query", "repoId"])
data class RepoRemoteKeys(
  val query: String,
  val repoId: Long,
  val prevKey: Int?,
  val nextKey: Int?,
  val position: Int
)
