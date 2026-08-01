plugins {
    alias(libs.plugins.convention.android.library)
}

android {
    namespace = "com.sofar.skin"
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.fetch2)
    implementation(libs.fetch2.okhttp)
}
