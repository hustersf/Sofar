package com.sofar.business.github.model

import com.google.gson.annotations.SerializedName

data class RepoSearchResponse(
  @SerializedName("total_count") val total: Int,
  @SerializedName("items") val items: List<Repo> = emptyList()
)
