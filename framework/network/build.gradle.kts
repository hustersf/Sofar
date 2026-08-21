plugins {
    alias(libs.plugins.convention.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.sofar.network.coroutine"
}

dependencies {
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.retrofit.rxjava2)
    implementation(libs.gson)
    implementation(libs.kotlinx.serialization)
    implementation(libs.retrofit.scalars)
    implementation(libs.retrofit.serialization)
    implementation(libs.retrofit.result)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlin.coroutines.android)
}
