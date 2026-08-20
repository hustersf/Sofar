package com.sofar.network.cache.retrofit

import com.sofar.network.cache.policy.LoadPolicy
import java.util.concurrent.TimeUnit

const val UNSET_TTL = -1L

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Cacheable(
  /**
   * 缓存有效期。
   * -1 表示未显式配置，使用全局默认值。
   */
  val ttl: Long = UNSET_TTL,

  /**
   * 缓存有效期的单位，默认为秒
   */
  val unit: TimeUnit = TimeUnit.SECONDS,

  /**
   * 缓存加载策略。
   * DEFAULT 表示使用全局默认策略。
   */
  val loadPolicy: LoadPolicy = LoadPolicy.DEFAULT
)
