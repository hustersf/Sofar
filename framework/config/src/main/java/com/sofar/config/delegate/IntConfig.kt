package com.sofar.config.delegate

class IntConfig(key: String? = null, def: Int = 0, group: String? = null) :
  BaseConfig<Int>(key, def, group) {
  override fun convert(raw: String): Int = raw.toInt()
}