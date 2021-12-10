package com.sofar.config.delegate

import com.google.gson.Gson
import com.sofar.config.ConfigManager
import com.sofar.config.internal.ConfigStorage
import com.sofar.config.internal.Util
import kotlin.reflect.KProperty

abstract class BaseConfig<T>(
  k: String? = null,
  private var def: T,
  private var group: String? = null,
) {

  companion object {
    val storage: ConfigStorage = ConfigManager.get().storage
    val parse: Gson = ConfigManager.get().gson
  }

  private lateinit var key: String

  init {
    if (k != null) {
      key = k
    }
  }

  operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
    checkKeyInit(property)
    var groupKey = Util.getKey(group, key)
    var raw = storage.getValue(groupKey) ?: return def
    return valueFromRaw(raw)
  }

  operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    checkKeyInit(property)
    var groupKey = Util.getKey(group, key)
    storage.setValue(groupKey, value?.let { serialize(it) })
  }

  private fun checkKeyInit(property: KProperty<*>) {
    if (!this::key.isInitialized) {
      key = property.name
    }
  }


  private fun valueFromRaw(raw: String): T {
    return try {
      convert(raw) ?: def
    } catch (e: Exception) {
      e.printStackTrace()
      def
    }
  }

  protected abstract fun convert(raw: String): T

  protected open fun serialize(value: T): String = value.toString()

}