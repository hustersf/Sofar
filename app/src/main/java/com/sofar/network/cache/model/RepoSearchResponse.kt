package com.sofar.network.cache.model

import com.google.gson.annotations.SerializedName

data class RepoSearchResponse(
  @SerializedName("total_count")
  val total: Int,

  @SerializedName("items")
  val items: List<Repo>,

  val nextPage: Int
)
