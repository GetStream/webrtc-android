@file:Suppress("UnstableApiUsage")

import io.getstream.Configurations

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  id(libs.plugins.android.application.get().pluginId)
  id(libs.plugins.kotlin.android.get().pluginId)
  id(libs.plugins.compose.compiler.get().pluginId)
  id(libs.plugins.baseline.profile.get().pluginId)
}

android {
  namespace = "io.getstream.webrtc.sample.compose"
  compileSdk = Configurations.compileSdk

  defaultConfig {
    applicationId = "io.getstream.webrtc.sample.compose"
    minSdk = Configurations.minSdk
    targetSdk = Configurations.targetSdk
    versionName = Configurations.versionName

    buildConfigField(
      "String",
      "SIGNALING_SERVER_IP_ADDRESS",
      "\"" + com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(
        rootDir,
        providers
      )
        .getProperty("SIGNALING_SERVER_IP_ADDRESS", "") + "\""
    )
  }

  buildFeatures {
    compose = true
    buildConfig = true
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  packaging {
    resources {
      excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    }
  }

  lint {
    abortOnError = false
  }

  buildTypes {
    create("benchmark") {
      signingConfig = signingConfigs.getByName("debug")
      matchingFallbacks += listOf("release")
      isDebuggable = false
    }
  }
}

dependencies {
  // webrtc
  implementation(project(":stream-webrtc-android"))
  implementation(project(":stream-webrtc-android-ui"))
  implementation(project(":stream-webrtc-android-compose"))
  implementation(project(":stream-webrtc-android-ktx"))

  // compose
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.tooling)
  implementation(libs.androidx.compose.material)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.foundation.layout)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.constraintlayout)

  // image loading
  implementation(libs.landscapist.glide)

  // okhttp
  implementation(libs.okhttp.logging)

  // coroutines
  implementation(libs.kotlinx.coroutines.android)

  // logger
  implementation(libs.stream.log)

  baselineProfile(project(":benchmark"))
}