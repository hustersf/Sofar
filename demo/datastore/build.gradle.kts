plugins {
    id("social.android.library")
}

android {
    namespace = "com.sofar.datastore"
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.datastore)
}
