plugins {
    alias(libs.plugins.convention.android.library)
}

android {
    namespace = "com.sofar.base"
    
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recyclerview)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.retrofit.gson)
    implementation(libs.rxjava)
}
