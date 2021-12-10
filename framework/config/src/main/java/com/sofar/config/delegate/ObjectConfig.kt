package com.sofar.config.delegate

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

open class ObjectConfig<T>(key: String? = null, def: T? = null, group: String? = null) :
  BaseConfig<T?>(key, def, group) {
  private val _type: Type =
    (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]

  companion object {
    inline fun <reified T> of(
      key: String? = null,
      def: T? = null,
      group: String? = null,
    ): ObjectConfig<T> {
      return object : ObjectConfig<T>(key, def, group) {}
    }
  }

  override fun convert(raw: String): T {
    return parse.fromJson(raw, _type)
  }

  override fun serialize(value: T?): String {
    return parse.toJson(value, _type)
  }

}