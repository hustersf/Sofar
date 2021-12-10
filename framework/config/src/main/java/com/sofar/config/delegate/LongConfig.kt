package com.sofar.config.delegate

class LongConfig(key: String? = null, def: Long = 0, group: String? = null) :
  BaseConfig<Long>(key, def, group) {
  override fun convert(raw: String): Long = raw.toLong()
}