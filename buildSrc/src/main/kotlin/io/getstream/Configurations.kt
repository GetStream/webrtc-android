package io.getstream

object Configurations {
    const val compileSdk = 35
    const val targetSdk = 35
    const val minSdk = 21
    const val appMinSdk = 24
    const val majorVersion = 1
    const val minorVersion = 3
    const val patchVersion = 5
    const val versionName = "$majorVersion.$minorVersion.$patchVersion"
    const val snapshotVersionName = "$majorVersion.$minorVersion.${patchVersion + 1}-SNAPSHOT"
    const val artifactGroup = "io.getstream"
}
