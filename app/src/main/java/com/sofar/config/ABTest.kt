@file: JvmName("ABTest")

package com.sofar.config

import com.sofar.config.delegate.IntConfig

private const val GROUP = "abtest"

var enterLastTab by IntConfig(group = GROUP)

