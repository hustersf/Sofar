plugins {
    alias(libs.plugins.convention.android.library)
}

android {
    namespace = "com.sofar.badge"
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recyclerview)

    implementation(libs.rxandroid)
    implementation(libs.rxjava)
    implementation(libs.greendao)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
