@file:Suppress("UnstableApiUsage")
pluginManagement {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
    maven(url = "https://plugins.gradle.org/m2/")
  }
}
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    maven(url = "https://plugins.gradle.org/m2/")
  }
}
rootProject.name = "stream-webrtc-android"
include(":app")
include(":stream-webrtc-android")
include(":stream-webrtc-android-ui")
include(":stream-webrtc-android-compose")
include(":stream-webrtc-android-ktx")
include(":stream-webrtc-android-bom")
//include(":benchmark")