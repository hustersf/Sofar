plugins {
    alias(libs.plugins.convention.android.library)
}

android {
    namespace = "com.sofar.startup"
}

dependencies {
    implementation(libs.androidx.appcompat)
}
