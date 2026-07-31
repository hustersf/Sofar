plugins {
    id("social.android.library")
}

android {
    namespace = "com.sofar.config"
}

dependencies {
    implementation(libs.gson)
    implementation(libs.androidx.annotation)
}
