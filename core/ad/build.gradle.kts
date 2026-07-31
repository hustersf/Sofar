plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.sofar.ad"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    implementation(files("libs/open_ad_sdk_v3.2.0.6.aar"))
    implementation(project(":framework:utility"))
    implementation(libs.androidx.annotation)

    implementation(libs.rxandroid)
    implementation(libs.rxjava)
}
