import com.android.build.api.dsl.LibraryExtension
import com.sofar.buildlogic.configureKotlinAndroid
import com.sofar.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

abstract class AndroidLibraryConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      pluginManager.apply("com.android.library")

      extensions.configure<LibraryExtension> {
        configureKotlinAndroid(this)

        defaultConfig {
          testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
          consumerProguardFiles("consumer-rules.pro")
        }

        buildTypes {
          getByName("release") {
            isMinifyEnabled = false // 库模块一般由主app最终执行混淆，本身设为 false 提升子模块增量编译速度
            proguardFiles(
              getDefaultProguardFile("proguard-android-optimize.txt"),
              "proguard-rules.pro"
            )
          }
        }

        // 自动资源前缀命名约束
        resourcePrefix = path
          .split("""\W""".toRegex())
          .drop(1)
          .distinct()
          .joinToString(separator = "_")
          .lowercase() + "_"
      }

      // 统一为全家所有子模块自动注入高频必装的基础依赖
      dependencies {
        // 自动拉取我们在 gradle/libs.versions.toml 中定义好的版本
        libs.findLibrary("androidx.annotation").ifPresent {
          "implementation"(it.get())
        }
      }
    }
  }
}
