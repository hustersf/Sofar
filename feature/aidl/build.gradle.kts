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
    implementation(libs.androidx.appcompat)
}
