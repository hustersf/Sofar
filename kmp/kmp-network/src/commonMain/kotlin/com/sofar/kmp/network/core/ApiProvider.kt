package com.sofar.kmp.network.core

import com.sofar.kmp.network.api.BannerApi
import com.sofar.kmp.network.internal.NetworkEngine

abstract class ApiProvider {
  @PublishedApi
  internal lateinit var engine: NetworkEngine

  val banner: BannerApi by lazy { BannerApi(engine) }
}
