package com.sofar.business.github.api

import com.sofar.network.retrofit.SofarRetrofitConfig
import io.reactivex.schedulers.Schedulers

class GithubRetrofitConfig : SofarRetrofitConfig("https://api.github.com/", Schedulers.newThread())
