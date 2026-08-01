plugins {
    alias(libs.plugins.convention.android.library)
}

android {
    namespace = "com.sofar.download"
}

dependencies {
    implementation(libs.androidx.appcompat)
}
