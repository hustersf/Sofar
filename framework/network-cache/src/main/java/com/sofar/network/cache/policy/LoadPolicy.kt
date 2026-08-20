package com.sofar.network.cache.policy

enum class LoadPolicy {
  DEFAULT,             // 哨兵值：在 @Cacheable 注解中使用，表示跟随 SDK 全局配置的策略。
  CACHE_THEN_NETWORK,  // 先返回缓存，再刷新网络
  NETWORK_ONLY,        // 强制网络
  CACHE_ONLY           // 仅读取缓存，不发起网络请求
}
