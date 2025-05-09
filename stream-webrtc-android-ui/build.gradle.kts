@file:Suppress("UnstableApiUsage")

import io.getstream.Configurations

@Suppress("DSL_SCOPE_VIOLATION") plugins {
  id(libs.plugins.android.library.get().pluginId)
  id(libs.plugins.kotlin.android.get().pluginId)
//  id(libs.plugins.baseline.profile.get().pluginId)
}

rootProject.extra.apply {
  set("PUBLISH_GROUP_ID", Configurations.artifactGroup)
  set("PUBLISH_ARTIFACT_ID", "stream-webrtc-android-ui")
  set("PUBLISH_VERSION", rootProject.extra.get("rootVersionName"))
}

apply(from = "${rootDir}/scripts/publish-module.gradle")

android {
  namespace = "io.getstream.webrtc.android.ui"
  compileSdk = Configurations.compileSdk

  defaultConfig {
    minSdk = Configurations.minSdk
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  lint {
    abortOnError = false
  }

//  baselineProfile {
//    baselineProfileOutputDir = "."
//    filter {
//      include("io.getstream.webrtc.android.ui.**")
//    }
//  }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions.freeCompilerArgs += listOf(
    "-Xexplicit-api=strict"
  )
}

tasks.withType(JavaCompile::class.java).configureEach {
  this.targetCompatibility = libs.versions.jvmTarget.get()
  this.sourceCompatibility = libs.versions.jvmTarget.get()
}

dependencies {
  api(project(":stream-webrtc-android"))

//  baselineProfile(project(":benchmark"))
}