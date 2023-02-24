import io.getstream.Configurations

plugins {
  `java-platform`
}

rootProject.extra.apply {
  set("PUBLISH_GROUP_ID", Configuration.artifactGroup)
  set("PUBLISH_ARTIFACT_ID", "stream-webrtc-android-bom")
  set("PUBLISH_VERSION", rootProject.extra.get("rootVersionName"))
}

dependencies {
  constraints {
    api(project(":stream-webrtc-android"))
    api(project(":stream-webrtc-android-ui"))
    api(project(":stream-webrtc-android-compose"))
    api(project(":stream-webrtc-android-ktx"))
  }
}

apply(from ="${rootDir}/scripts/publish-module.gradle")

