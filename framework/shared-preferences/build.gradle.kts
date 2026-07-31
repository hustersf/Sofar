plugins {
    id("social.android.library")
}

android {
    namespace = "com.sofar.preferences"
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.gson)
    implementation(libs.mmkv)
}
