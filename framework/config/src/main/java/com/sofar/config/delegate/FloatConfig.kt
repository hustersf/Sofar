package com.sofar.config.delegate

class FloatConfig(key: String? = null, def: Float = 0f, group: String? = null) :
  BaseConfig<Float>(key, def, group) {
  override fun convert(raw: String): Float = raw.toFloat()
}