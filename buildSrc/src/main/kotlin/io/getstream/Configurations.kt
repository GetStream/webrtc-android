package io.getstream

object Configurations {
    const val compileSdk = 34
    const val targetSdk = 34
    const val minSdk = 21
    const val majorVersion = 1
    const val minorVersion = 2
    const val patchVersion = 1
    const val versionName = "$majorVersion.$minorVersion.$patchVersion"
    const val snapshotVersionName = "$majorVersion.$minorVersion.${patchVersion + 1}-SNAPSHOT"
    const val artifactGroup = "io.getstream"
}
