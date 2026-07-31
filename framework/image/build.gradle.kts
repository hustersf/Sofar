plugins {
    id("social.android.library")
}

android {
    namespace = "com.sofar.image"
}

dependencies {
    implementation(libs.fresco)
    implementation(libs.androidx.annotation)
}
