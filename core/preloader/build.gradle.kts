plugins {
    alias(libs.plugins.convention.android.library)
}

android {
    namespace = "com.sofar.preloader"
}

dependencies {
    implementation(libs.rxandroid)
    implementation(libs.rxjava)
}
