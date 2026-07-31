pluginManagement {
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

include(":framework:widget")
include(":framework:utility")
include(":framework:network")
include(":framework:network2")
include(":framework:download")
include(":framework:skin")
include(":framework:audio-record")
include(":framework:image")
include(":framework:shared-preferences")
include(":framework:config")
include(":framework:startup")

include(":core:base")
include(":core:fun")
include(":core:preloader")
include(":core:ad")

include(":library:daogenerator")
include(":library:mpchart")

include(":debug")
include(":tool:profiler")

include(":demo:aidl")
include(":demo:datastore")
include(":demo:appwidget")
include(":demo:room")
include(":kmp:kmp-network")
