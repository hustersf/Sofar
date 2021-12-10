package com.sofar.config.delegate

class BooleanConfig(key: String? = null, def: Boolean = false, group: String? = null) :
  BaseConfig<Boolean>(key, def, group) {
  override fun convert(raw: String): Boolean = raw.toBoolean()
}