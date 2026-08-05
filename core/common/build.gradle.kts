plugins {
    alias(libs.plugins.convention.android.library)
}

android {
    namespace = "com.sofar.common"
}

dependencies {
    implementation(libs.androidx.annotation)
}
