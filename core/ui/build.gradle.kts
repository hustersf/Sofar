plugins {
    alias(libs.plugins.convention.android.library)
}

android {
    namespace = "com.sofar.ui"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recyclerview)
    implementation(libs.dynamicanimation.ktx)
}
