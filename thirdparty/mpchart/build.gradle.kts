plugins {
    alias(libs.plugins.convention.android.library)
}

android {
    namespace = "com.sofar.chart"
}

dependencies {
    implementation(libs.androidx.annotation)
}
