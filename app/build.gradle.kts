plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.sofar"
    compileSdk = libs.versions.compileSdk.get().toString().toInt()

    defaultConfig {
        applicationId = "com.sofar"
        minSdk = libs.versions.minSdk.get().toString().toInt()
        targetSdk = libs.versions.targetSdk.get().toString().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    kotlin {
        jvmToolchain(17)
    }

    // 签名信息配置
    signingConfigs {
        create("myConfig") {
            storeFile = file("$rootDir/sofar.keystore")
            storePassword = "123456"
            keyAlias = "sofar"
            keyPassword = "123456"
            enableV1Signing = true
            enableV2Signing = true
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("myConfig")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        release {
            signingConfig = signingConfigs.getByName("myConfig")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    // 基础核心与业务模块
    implementation(project(":core:base"))
    implementation(project(":core:badge"))
    implementation(project(":core:preloader"))
    implementation(project(":core:ad"))

    // 基础框架模块
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":framework:network-rxjava"))
    implementation(project(":framework:network-coroutine"))
    implementation(project(":framework:download"))
    implementation(project(":framework:skin"))
    implementation(project(":core:audio-record"))
    implementation(project(":framework:image"))
    implementation(project(":framework:shared-preferences"))
    implementation(project(":framework:config"))

    // 调试与演示模块
    debugImplementation(project(":tool:profiler"))
    implementation(project(":feature:aidl"))
    implementation(project(":feature:datastore"))
    implementation(project(":feature:appwidget"))
    implementation(project(":feature:room"))

    // 三方封装库与 KMP 模块
    implementation(project(":thirdparty:mpchart"))
    implementation(project(":framework:network-kmp"))

    // AndroidX & Material 组件
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.material)

    // Room
    implementation(libs.androidx.room)
    ksp(libs.androidx.room.compiler)

    // 图片加载与底层库
    implementation(libs.glide)
    implementation(libs.relinker)
    implementation(libs.fresco)
    implementation(libs.fresco.animated)
    implementation(libs.fresco.gif)

    // 网络与异步核心
    implementation(libs.rxjava)
    implementation(libs.rxandroid)
    implementation(libs.okhttp)
    implementation(libs.gson)
    implementation(libs.retrofit)

    // 测试相关
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
