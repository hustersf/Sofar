
# Network Cache SDK 接入文档

---

## 1. SDK 介绍

### 1.1 能力概述

Network Cache SDK 是基于 **Retrofit + Kotlin Flow** 的网络响应缓存框架，提供开箱即用的两级缓存能力（内存 + 磁盘），支持灵活的缓存策略配置。

核心能力：

| 能力 | 说明 |
|---|---|
| 两级缓存 | 内存 LruCache + 磁盘 LruCache，自动级联读写 |
| 缓存策略 | 支持先缓存后网络 / 仅网络 / 仅缓存三种模式 |
| TTL 控制 | 注解级别精确控制缓存有效期 |
| 磁盘加密 | 磁盘文件默认开启 AES-GCM-256 加密，SDK 使用 Keystore 保护软密钥 |
| Fail Safe | 缓存能力异常时自动降级为纯网络请求，不影响业务 |
| 响应去重 | 可选过滤连续相同响应，避免 UI 重复刷新 |
| 监控上报 | 提供命中率、网络耗时、读写异常等关键节点回调 |

### 1.2 设计原则

- **零风险接入**：SDK 是增强功能，而非强依赖。未初始化或未配置注解时，自动退化为 Retrofit 原生 Flow 行为。
- **渐进增强**：满足接入条件时启用缓存，不满足时静默退化。
- **单一职责**：注解控制接入，配置控制行为，互不干扰。

---

## 2. 快速集成

### 2.1 添加依赖

```kotlin
// build.gradle.kts
implementation("com.sofar:network-cache:0.1.0")   // 后续发布
```

### 2.2 初始化 SDK

在 `Application.onCreate()` 中初始化，重复调用无效：

```kotlin
NetworkCache.init(
    NetworkCache.Builder(
        cacheDir = File(externalCacheDir, "network_cache")
    ).build()
)
```

### 2.3 配置 Retrofit

在 Retrofit 构建时添加 `CacheFlowCallAdapterFactory`：

```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl("https://api.example.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(CacheFlowCallAdapterFactory.create()) // 添加这一行
    .build()
```

### 2.4 声明缓存接口

在 Retrofit 接口方法上添加 `@Cacheable` 注解，返回值改为 `Flow<T>`：

```kotlin
interface UserService {

    @Cacheable                          // 开启缓存，使用默认配置（TTL 5min，先缓存后网络）
    @GET("users/profile")
    fun getUserProfile(): Flow<UserResponse>
}
```

### 2.5 收集数据

```kotlin
lifecycleScope.launch {
    userService.getUserProfile()
        .catch { e -> /* 处理错误 */ }
        .onCompletion { /* Flow 结束（无论是否有数据） */ }
        .collect { response ->
            // 收到次数取决于 LoadPolicy，见下方说明
            updateUI(response)
        }
}
```

> **collect 触发次数说明**：
> - `CACHE_THEN_NETWORK`：最多 **2 次**（缓存命中 + 网络返回）；缓存未命中时 **1 次**（仅网络）
> - `NETWORK_ONLY`：固定 **1 次**（网络返回）
> - `CACHE_ONLY`：**0 或 1 次**（有缓存则 1 次，无缓存则 0 次，Flow 正常结束）
>
> 缓存未命中时不会触发 `catch`，Flow 会正常走完并调用 `onCompletion`。

### 2.6 异常处理

Network Cache SDK 基于 Kotlin Flow 实现，异常处理遵循 Flow 标准行为，建议使用 `catch` 统一处理：

```kotlin
userService.getUserProfile()
    .catch { throwable ->
        showError(throwable.message)
    }
    .collect { response ->
        updateUI(response)
    }
```

可捕获的异常包括：
- 网络异常（连接超时、断网等）
- HTTP 异常（4xx / 5xx）
- 数据解析异常
- `EmptyBodyException`：HTTP 状态码为 200，但响应体为 null 时抛出

```kotlin
userService.getUserProfile()
    .catch { throwable ->
        when (throwable) {
            is EmptyBodyException -> { /* 响应体为空 */ }
            is HttpException      -> { /* HTTP 错误 */ }
            else                  -> { /* 其他异常 */ }
        }
    }
    .collect { response -> updateUI(response) }
```

---

## 3. 高级用法

### 3.1 缓存策略（LoadPolicy）

通过 `@Cacheable(loadPolicy = ...)` 配置加载策略：

```kotlin
interface DataService {

    // 先返回缓存，再刷新（默认）
    @Cacheable(loadPolicy = LoadPolicy.CACHE_THEN_NETWORK)
    @GET("data/list")
    fun getList(): Flow<ListResponse>

    // 强制请求网络，忽略缓存（适合实时性要求高的接口）
    @Cacheable(loadPolicy = LoadPolicy.NETWORK_ONLY)
    @GET("data/realtime")
    fun getRealtime(): Flow<RealtimeResponse>

    // 仅读取缓存，不发起网络请求（适合离线场景）
    @Cacheable(loadPolicy = LoadPolicy.CACHE_ONLY)
    @GET("data/offline")
    fun getOffline(): Flow<OfflineResponse>
}
```

| LoadPolicy | 说明 | collect 次数 |
|---|---|---|
| `DEFAULT` | 注解层哨兵值，跟随 SDK 全局配置的策略，不可传入 `setLoadPolicy()` | 取决于全局策略 |
| `CACHE_THEN_NETWORK` | 有效缓存立即返回，同时**始终**发起网络请求并更新缓存 | 缓存命中最多 2 次，未命中 1 次 |
| `NETWORK_ONLY` | 不读缓存，直接请求网络，结果仍会写入缓存 | 1 次 |
| `CACHE_ONLY` | 只读缓存，不发起网络请求，无缓存时 Flow 正常结束不报错 | 0 或 1 次 |

#### 动态覆盖策略（@Tag）

通过 `@Tag` 形参，可在调用时动态覆盖注解中声明的默认策略，常用于下拉刷新等强制跳过缓存的场景：

```kotlin
interface DataService {
    @Cacheable
    @GET("data/list")
    fun getList(
        @Tag loadPolicy: LoadPolicy? = null  // null 时使用 @Cacheable 声明的策略
    ): Flow<ListResponse>
}

// 正常调用 —— 使用 @Cacheable 的 loadPolicy
dataService.getList()

// 下拉刷新 —— 强制走网络
dataService.getList(loadPolicy = LoadPolicy.NETWORK_ONLY)
```

### 3.2 TTL 配置

```kotlin
@Cacheable(ttl = 5, unit = TimeUnit.MINUTES)   // 缓存 5 分钟
@GET("data/config")
fun getConfig(): Flow<ConfigResponse>

@Cacheable(ttl = 24, unit = TimeUnit.HOURS)    // 缓存 24 小时
@GET("data/static")
fun getStaticData(): Flow<StaticResponse>
```

注解未配置时，使用 SDK 全局默认值：`ttl = 300`，`unit = TimeUnit.SECONDS`（等价于 5 分钟）。

可通过 Builder 统一修改全局默认 TTL：

```kotlin
NetworkCache.Builder(cacheDir)
    .setTtl(10, TimeUnit.MINUTES)   // 将全局默认 TTL 改为 10 分钟
    .build()
```

> **磁盘落盘门限**：满足以下两个条件才会持久化到磁盘，仅内存缓存不受此限制：
> - TTL ≥ 5 分钟
> - 响应体大小 ≤ 512KB
>
> TTL 过短或响应体过大时，数据只写入内存缓存，进程退出后不保留。

### 3.3 响应写入控制（CachePredicate）

通过 `setCachePredicate` 决定当次响应是否写入缓存。适用于只缓存业务成功响应（如 `errorCode == 0`）的场景：

```kotlin
NetworkCache.init(
    NetworkCache.Builder(cacheDir)
        .setCachePredicate { response ->
            when (response) {
                is BaseResponse<*> -> response.errorCode == 0  // 只有业务成功才写缓存
                else -> true
            }
        }
        .build()
)
```

> **说明**：未配置时默认所有成功响应均写入缓存。响应数据仍会正常返回给业务，`CachePredicate` 只影响写盘行为。

### 3.4 响应去重

开启后，`CACHE_THEN_NETWORK` 策略下若网络响应与缓存数据相同，不会触发第二次 `collect`，避免 UI 重复刷新：

```kotlin
NetworkCache.Builder(cacheDir)
    .setDeduplicateResponse(true)   // 默认 false
    .build()
```

> **前提**：DTO 需正确实现 `equals()` 方法（推荐使用 `data class`）。

### 3.5 缓存监控（ICacheMonitor）

接入 App 的 APM 或埋点系统，监控缓存运行状态：

```kotlin
NetworkCache.Builder(cacheDir)
    .setMonitor(object : ICacheMonitor {
        override fun onCacheHit(urlPath: String) {
            // 缓存命中，统计命中率
            apm.trackCacheHit(urlPath)
        }
        override fun onCacheMiss(urlPath: String) {
            // 缓存未命中
        }
        override fun onCacheExpired(urlPath: String) {
            // 缓存已过期
        }
        override fun onCacheReadFailed(urlPath: String, throwable: Throwable) {
            // 缓存读取失败
        }
        override fun onCacheWriteFailed(urlPath: String, throwable: Throwable) {
            // 缓存写入失败，上报异常
            apm.reportException(throwable)
        }
        override fun onNetworkSuccess(urlPath: String, costMs: Long) {
            // 网络请求成功，统计耗时
            apm.trackNetworkCost(urlPath, costMs)
        }
        override fun onNetworkFailed(urlPath: String, throwable: Throwable, costMs: Long) {
            // 网络请求失败
        }
    })
    .build()
```

### 3.6 日志配置

```kotlin
// 开启内置调试日志（仅 Debug 包建议开启）
NetworkCache.Builder(cacheDir)
    .setLogger(DefaultSdkLogger(isDebug = BuildConfig.DEBUG))
    .build()

// 接入自定义日志系统
NetworkCache.Builder(cacheDir)
    .setLogger(object : ISdkLogger {
        override fun d(tag: String, msg: String) { MyLogger.d(tag, msg) }
        override fun w(tag: String, msg: String) { MyLogger.w(tag, msg) }
        override fun e(tag: String, msg: String, throwable: Throwable?) {
            MyLogger.e(tag, msg, throwable)
        }
    })
    .build()
```

### 3.7 缓存容量配置

```kotlin
NetworkCache.Builder(cacheDir)
    .setMaxMemorySize(8 * 1024 * 1024)   // 内存上限 8MB
    .setMaxDiskSize(100 * 1024 * 1024L)  // 磁盘上限 100MB
    .build()
```

| 配置方法 | 说明 | 默认值 |
|---|---|---|
| `setMaxMemorySize` | 内存缓存上限（字节） | `Runtime.maxMemory() / 16`，区间 [4MB, 32MB] |
| `setMaxDiskSize` | 磁盘缓存上限（字节） | 50MB |

### 3.8 磁盘加密配置

磁盘缓存默认开启 **AES-GCM-256** 加密。SDK 会生成 32 字节软密钥，并使用 Android Keystore 对该软密钥加密保护。

```kotlin
// 默认：磁盘加密开启（推荐，生产环境保持默认）
NetworkCache.Builder(cacheDir)
    .build()

// 关闭磁盘加密（仅适用于纯内网设备或调试环境）
NetworkCache.Builder(cacheDir)
    .disableDiskEncryption()
    .build()
```

> **注意**：加密状态切换（开→关 或 关→开）后，原有磁盘缓存将无法解密，会静默降级为缓存未命中。

#### 加密设计说明

| 项目 | 说明 |
|---|---|
| 算法 | AES-256-GCM（认证加密，防篡改） |
| 密钥管理 | SDK 生成 32 字节软密钥；文件加解密使用软密钥，软密钥落盘前再用 Android Keystore 加密保护 |
| 磁盘格式 | `[IV 12字节][密文 + GCM Tag 16字节]` |
| 加密时机 | 写盘时加密，读取时解密，内存层不加密 |
| 性能开销 | 单次写入 ≤ 2ms（512KB 响应），详见第 6 节性能数据 |

### 3.9 清除缓存

账号切换、退出登录等身份变化场景，建议主动清除缓存，避免复用旧数据。

原因是缓存 Key 由 `CacheKeyGenerator` 基于以下字段生成：
- `HTTP Method`
- `Request URL`（含 Query 参数）
- 文本类型 `RequestBody`（Json/Form/Xml）

默认不包含用户身份字段（如用户 ID、账号态 Header/Token）。因此当两位用户请求同一接口且请求参数相同时，可能命中同一份历史缓存。为避免跨账号复用缓存数据，建议在身份变化后立即清理缓存。

```kotlin
NetworkCache.clearAll()   // 清除全部内存 + 磁盘缓存
```

---

## 4. 完整示例

```kotlin
// Application.kt
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NetworkCache.init(
            NetworkCache.Builder(
                cacheDir = File(externalCacheDir, "network_cache")
            )
            .setLogger(DefaultSdkLogger(isDebug = BuildConfig.DEBUG))
            .setDeduplicateResponse(true)
            .setCachePredicate { response ->
                when (response) {
                    is BaseResponse<*> -> response.errorCode == 0
                    else -> true
                }
            }
            .build()
        )
    }
}

// ApiService.kt
interface ApiService {
    @Cacheable(ttl = 5, unit = TimeUnit.MINUTES)
    @GET("users/profile")
    fun getUserProfile(): Flow<UserResponse>
}

// ViewModel.kt
viewModelScope.launch {
    apiService.getUserProfile()
        .catch { e -> showError(e.message) }
        .collect { response -> updateUI(response) }
}
```

---

## 5. 常见问题

**Q：SDK 未初始化或接口未加 `@Cacheable`，会影响正常请求吗？**

A：不会。未初始化或无注解时，SDK 自动退化为 Retrofit 原生 Flow 行为，缓存能力失效不影响接口调用。

**Q：`CACHE_THEN_NETWORK` 策略下 `collect` 会触发几次？**

A：缓存命中时最多两次（第一次为缓存数据，第二次为网络数据）；缓存未命中时固定一次（仅网络数据）。开启 `deduplicateResponse` 后，若两次数据相同则只触发一次。

**Q：`CACHE_ONLY` 无缓存时会报错吗？**

A：不会。无缓存时 Flow 正常结束，不发射数据，不抛出异常。

**Q：多模块项目重复调用 `init()` 会怎样？**

A：第一次调用生效，后续调用自动忽略，安全幂等。

**Q：`Flow<Unit>` 类型的接口是否支持 `@Cacheable`？**

A：不建议。`Unit` 类型无实际响应内容，缓存无意义，请勿添加 `@Cacheable` 注解。

---

## 6. 性能参考数据

---

### 6.1 磁盘存储性能测试（Instrumented）

> 使用 `DiskCacheStoragePerfTest`（Instrumented Test）在两台真实设备上运行，场景覆盖大文件、小文件、LRU 驱逐三种负载，加密与明文对照，写入与读取分开统计。测试直接调用 `DiskCacheStorage.put/get`，专注测量磁盘 I/O 与加解密本身耗时，不含协程调度开销。

#### 测试环境

| 设备 | 芯片 | Android |
|---|---|---|
| Samsung SM-A536U1 | Snapdragon 778G | Android 14 |
| Google Pixel 8 | Tensor G3 | Android 15 |

#### 写入性能对比（avg/file）

明文与 AES-GCM-256 软件密钥加密横向对比：

| 场景 | 文件规模 | Samsung 明文 | Samsung 加密 | 加密开销 | Pixel 8 明文 | Pixel 8 加密 | 加密开销 |
|---|---|---|---|---|---|---|---|
| 大文件 | 512KB × 10 | 6ms | 7ms | +1ms | 6ms | 6ms | ≈ 0 |
| 大文件 | 512KB × **50** | 3ms | 5ms | +2ms | 2ms | 3ms | +1ms |
| 小文件 | 4KB × 200 | 1ms | 1ms | ≈ 0 | 1ms | 1ms | ≈ 0 |
| 小文件 | 4KB × **1000** | <1ms | <1ms | ≈ 0 | <1ms | <1ms | ≈ 0 |
| LRU 驱逐 | 256KB × 200（共 50MB） | 2ms | 4ms | +2ms | 1ms | 3ms | +2ms |

#### 读取性能对比（avg/file）

| 场景 | 文件规模 | Samsung 明文 | Samsung 加密 | 解密开销 | Pixel 8 明文 | Pixel 8 加密 | 解密开销 |
|---|---|---|---|---|---|---|---|
| 大文件 | 512KB × 10 | 2ms | 4ms | +2ms | 1ms | 2ms | +1ms |
| 大文件 | 512KB × **50** | 2ms | 3ms | +1ms | 1ms | 2ms | +1ms |
| 小文件 | 4KB × 200 | 0ms | 0ms | ≈ 0 | 0ms | 0ms | ≈ 0 |
| 小文件 | 4KB × **1000** | 0ms | 0ms | ≈ 0 | 0ms | 0ms | ≈ 0 |
| LRU 驱逐 | 256KB × 200 | 1ms | 2ms | +1ms | 0ms | 1ms | +1ms |

#### Android Keystore 硬件加密 vs 软件密钥对比

> 为减少“批量文件与文件系统行为”的干扰，这里补充 **单文件（fileCount=1）** 的对比数据，更直观反映两种加密本身差异。
> 当前解密路径已使用 `copyOfRange` 进行字节切分，避免 `take/drop` 带来的额外集合转换开销。

**单文件端到端对比（Samsung SM-A536U1）**

| 场景（1 file） | 方案 | write avg/file | seq read avg/file | rnd read avg/file |
|---|---|---|---|---|
| 512KB | 软件密钥 | 48ms | 6ms | 5ms |
| 512KB | Keystore | 96ms | 41ms | 40ms |
| 4KB | 软件密钥 | 43ms | 2ms | 1ms |
| 4KB | Keystore | 63ms | 8ms | 7ms |

**加解密算子分解（同一设备日志）**

| 场景 | 方案 | 关键日志 |
|---|---|---|
| 512KB | Keystore | `init done: key=16ms`; `encrypt: init=4ms, doFinal=29ms, total=33ms`; `decrypt: init=2~3ms, doFinal=32~33ms, total=36ms` |
| 512KB | 软件密钥 | `secretKey created: cost=0ms`; `encrypt total=2ms`; `decrypt total=1ms` |
| 4KB | Keystore | `init done: key=14ms`; `encrypt: init=4ms, doFinal=3ms, total=7ms`; `decrypt: init=3ms, doFinal=2ms, total=6ms` |
| 4KB | 软件密钥 | `encrypt total=1ms`; `decrypt total=0ms` |

> 结论：Keystore 的耗时主要集中在 `cipher.init` 与 `doFinal` 两个阶段，其中 `init` 属于固定成本，`doFinal` 会随数据量增大而明显上升；软件密钥在这两个阶段基本都接近 0~1ms。

#### 总结

1. **软件密钥的加解密开销很低**：在 6.1 的批量测试里，写入与读取的额外开销大多为 0~2ms。
2. **文件数量变多不会明显抬高 per-file 读写耗时**：大文件从 10→50、小文件从 200→1000 后，主要变化体现在总耗时增长，单文件平均耗时整体仍然稳定。
3. **典型 API 响应（10~50KB）使用软件密钥时，加密开销基本可忽略**，通常低于应用层序列化、反序列化与调度成本。
4. **Keystore 的耗时主要集中在 `cipher.init` 与 `doFinal`**：其中 `init` 更像固定成本，`doFinal` 会随数据量增大而明显上升。
5. **单文件实测下，软件密钥明显快于 Keystore**：写入约快 1.5\~2.0 倍，读取约快 4~8 倍。
6. **生产环境中所有磁盘操作均在 IO 协程执行**，不会阻塞 UI 线程。

---

### 6.2 端到端链路耗时（真实应用）

> 以下数据来自 Samsung SM-A536U1 真实应用场景，从 `sendBtn` 点击到 `collect` 收到缓存数据的完整链路耗时，基于 Logcat 时间戳逐行计算。

#### 点击到数据到达对比

| 阶段 | 冷启动首次（48ms） | 非首次（18ms） | 说明 |
|---|---|---|---|
| 协程调度 | 3ms | 2ms | `lifecycleScope.launch` |
| Retrofit 反射解析 | 7ms | 0ms | 首次解析注解，后续方法已缓存 |
| Adapter 调度（adapt → callbackFlow → readCache → CacheStorageManager） | 17ms | 7ms | 首次调用需加载相关类并完成 JIT 编译，后续耗时随机器状态波动 |
| **磁盘读取 + AES 解密** | **5ms** | **3ms** | 含文件 I/O 和 GCM 解密 |
| **Gson 反序列化** | **14ms** | **6ms** | `ByteArray → JSON → DTO` |
| **合计** | **48ms** | **18ms** | — |

> 各阶段耗时基于 Logcat 毫秒时间戳计算，逐行累加与 `collect` 实测值差异 ≤ 2ms，属于线程调度和时间戳精度的正常误差。

#### 关键结论

- **磁盘读取 + 解密始终 ≤ 5ms**，加密引入的开销可忽略不计
- **冷启动 48ms 的主要成本是 Retrofit 反射（7ms）和类加载/JIT 编译（7~17ms）**，与缓存实现无关
- **非首次命中稳定在 18ms**，比发起一次网络请求（200ms+）快 **10 倍以上**
- Gson 反序列化（6~14ms）是持续存在的成本，属于 Retrofit 序列化框架固有开销
