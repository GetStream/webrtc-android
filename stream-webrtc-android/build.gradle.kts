@file:Suppress("UnstableApiUsage")

import io.getstream.Configurations

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  id(libs.plugins.android.library.get().pluginId)
  id(libs.plugins.kotlin.android.get().pluginId)
}

rootProject.extra.apply {
  set("PUBLISH_GROUP_ID", Configurations.artifactGroup)
  set("PUBLISH_ARTIFACT_ID", "stream-webrtc-android")
  set("PUBLISH_VERSION", rootProject.extra.get("rootVersionName"))
}

apply(from ="${rootDir}/scripts/publish-module.gradle")

android {
  namespace = "org.webrtc"
  compileSdk = Configurations.compileSdk

  defaultConfig {
    minSdk = Configurations.minSdk
    consumerProguardFiles("consumer-rules.pro")
  }

  sourceSets {
    getByName("main") {
      jniLibs.srcDir("libs")
    }
  }

  lint {
    abortOnError = false
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
}

tasks.withType(JavaCompile::class.java).configureEach {
  this.targetCompatibility = libs.versions.jvmTarget.get()
  this.sourceCompatibility = libs.versions.jvmTarget.get()
}

dependencies {
  implementation(libs.androidx.annotation)
}