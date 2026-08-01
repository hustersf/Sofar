plugins {
    alias(libs.plugins.convention.android.library)
}

android {
    namespace = "com.sofar.widget"

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":framework:utility"))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recyclerview)
    implementation(libs.dynamicanimation.ktx)
}
