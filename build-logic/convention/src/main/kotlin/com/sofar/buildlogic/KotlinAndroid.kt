package com.sofar.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

/**
 * 💡 100% 复刻并升级 NIA：为全家所有 Android 模块统一配置 Java/Kotlin 环境
 */
internal fun Project.configureKotlinAndroid(
  commonExtension: CommonExtension,
) {
  commonExtension.apply {
    compileSdk = libs.findVersion("compileSdk").get().requiredVersion.toInt()
    defaultConfig.apply {
      minSdk = libs.findVersion("minSdk").get().requiredVersion.toInt()
    }

    compileOptions.apply {
      sourceCompatibility = JavaVersion.VERSION_17
      targetCompatibility = JavaVersion.VERSION_17
    }
  }

  // 🚀 调用 NIA 灵魂内联函数，自动打理 Kotlin 编译器
  configureKotlin<KotlinAndroidProjectExtension>()
}

/**
 * 💡 为全家所有纯 JVM 纯 Kotlin/Java (非Android) 模块统一配置环境
 */
internal fun Project.configureKotlinJvm() {
  // 纯 JVM 模块同样要先挂载 kotlin 插件
  pluginManager.apply("org.jetbrains.kotlin.jvm")

  extensions.configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  configureKotlin<KotlinJvmProjectExtension>()
}

/**
 * 🧩 NIA 终极精髓：利用 Kotlin 泛型内联，强类型抹平 Android 扩展与 JVM 扩展的配置断层
 */
private inline fun <reified T : KotlinBaseExtension> Project.configureKotlin() = configure<T> {
  // 读取主工程 gradle.properties 中的警告标志，默认关闭
  val warningsAsErrors = providers.gradleProperty("warningsAsErrors").map {
    it.toBoolean()
  }.orElse(false)

  // 完美适配 Gradle 9.x：获取各平台唯一的 compilerOptions 强类型编译器对象
  val compilerOptions = when (this) {
    is KotlinAndroidProjectExtension -> compilerOptions
    is KotlinJvmProjectExtension -> compilerOptions
    else -> TODO("不支持的编译插件扩展: $this")
  }

  compilerOptions.apply {
    // 全线对齐 JVM 17 编译目标
    jvmTarget.set(JvmTarget.JVM_17)
    allWarningsAsErrors.set(warningsAsErrors)

    // 统一附赠大厂高频协程与前沿实验性 API 的 Opt-in 豁免，写代码时不再需要频繁在类名上加注解
    freeCompilerArgs.addAll(
      "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
      "-Xconsistent-data-class-copy-visibility" // 消除旧版数据类升级的警告
    )
  }
}
