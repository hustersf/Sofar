plugins {
  alias(libs.plugins.convention.android.library)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.kotlin.compose)
}

android {
  namespace = "com.sofar.kmp.demo"
}

dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.material.icons.extended)

  implementation(libs.androidx.navigation3.runtime)
  implementation(libs.androidx.navigation3.ui)
  implementation(libs.androidx.lifecycle.viewmodel.navigation3)

  implementation(libs.kotlinx.serialization)
}
