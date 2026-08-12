plugins {
    alias(libs.plugins.convention.android.library)
}

android {
    namespace = "com.sofar.aidl"

    buildFeatures {
        aidl = true
    }
}

dependencies {
    implementation(project(":core:ui"))

    implementation(libs.androidx.appcompat)
}
