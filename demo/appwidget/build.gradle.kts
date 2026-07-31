plugins {
    id("social.android.library")
}

android {
    namespace = "com.sofar.appwidget"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
}
