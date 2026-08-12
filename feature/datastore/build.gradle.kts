plugins {
    alias(libs.plugins.convention.android.library)
}

android {
    namespace = "com.sofar.datastore"
}

dependencies {
    implementation(project(":core:ui"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.datastore)
}
