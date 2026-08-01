plugins {
    alias(libs.plugins.convention.android.library)
}

android {
    namespace = "com.sofar.utility"
}

dependencies {
    implementation(libs.androidx.annotation)
}
