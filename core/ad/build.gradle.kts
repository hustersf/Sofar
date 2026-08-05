plugins {
    alias(libs.plugins.convention.android.library)
}

android {
    namespace = "com.sofar.ad"
}

dependencies {
    implementation(files("libs/open_ad_sdk_v3.2.0.6.aar"))
    implementation(project(":core:common"))
    implementation(libs.androidx.annotation)

    implementation(libs.rxandroid)
    implementation(libs.rxjava)
}
