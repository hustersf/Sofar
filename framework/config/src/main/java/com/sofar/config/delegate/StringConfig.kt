package com.sofar.config.delegate

class StringConfig(key: String? = null, def: String = "", group: String? = null) :
  BaseConfig<String>(key, def, group) {
  override fun convert(raw: String): String = raw
}