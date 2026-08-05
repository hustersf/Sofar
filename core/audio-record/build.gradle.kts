plugins {
    alias(libs.plugins.convention.android.library)
}

android {
    namespace = "com.sofar.audio.record"
}

dependencies {
    implementation(libs.androidx.annotation)
}
