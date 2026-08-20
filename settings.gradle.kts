pluginManagement {
    includeBuild("build-logic")
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "Sofar"

include(":app")

include(":framework:network-rxjava")
include(":framework:network-coroutine")
include(":framework:network-kmp")
include(":framework:download")
include(":framework:skin")
include(":framework:image")
include(":framework:shared-preferences")
include(":framework:config")
include(":framework:startup")
include(":framework:disklrucache-kmp")
include(":framework:network-cache")

include(":core:base")
include(":core:ui")
include(":core:common")
include(":core:audio-record")
include(":core:badge")
include(":core:preloader")
include(":core:ad")

include(":thirdparty:mpchart")

include(":tool:profiler")
include(":tool:daogenerator")

include(":feature:aidl")
include(":feature:datastore")
include(":feature:appwidget")
include(":feature:room")
