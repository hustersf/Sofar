package com.sofar.network2.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Banner(
  @SerialName("id") val id: Int,
  @SerialName("title") val title: String,
  @SerialName("desc") val desc: String,
  @SerialName("imagePath") val imageUrl: String,
  @SerialName("url") val url: String,
  @SerialName("order") val order: Int
)