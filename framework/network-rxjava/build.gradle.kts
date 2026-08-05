plugins {
    alias(libs.plugins.convention.android.library)
}

android {
    namespace = "com.sofar.network.rxjava"
}

dependencies {
    implementation(libs.androidx.annotation)
    implementation(libs.rxandroid)
    implementation(libs.rxjava)
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.retrofit.rxjava2)
    implementation(libs.retrofit.scalars)
}
