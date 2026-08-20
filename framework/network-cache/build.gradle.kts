plugins {
    alias(libs.plugins.convention.android.library)
}

android {
    namespace = "com.sofar.network.cache"
}

dependencies {
    implementation(project(":framework:disklrucache-kmp"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.gson)
    implementation(libs.kotlin.coroutines.android)

    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
