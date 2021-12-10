@file: JvmName("SystemConfig")

package com.sofar.config

import com.sofar.`fun`.play.Feed
import com.sofar.config.delegate.ObjectConfig
import com.sofar.config.delegate.StringConfig

var feed by ObjectConfig.of<Feed>()

var title by StringConfig(group = "feed")