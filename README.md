![AndroidWebRTC-1200x630px](https://user-images.githubusercontent.com/24237865/218683564-e4279cf9-51c2-4b48-9ca4-45a258a6898a.jpg)

<h1 align="center">Stream WebRTC Android</h1></br>

<p align="center">
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=21"><img alt="API" src="https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat"/></a>
  <a href="https://getstream.io"><img src="https://img.shields.io/endpoint?url=https://gist.githubusercontent.com/HayesGordon/e7f3c4587859c17f3e593fd3ff5b13f4/raw/11d9d9385c9f34374ede25f6471dc743b977a914/badge.json" alt="Stream Feeds"></a>
</p>

**Stream WebRTC Android** is Google's WebRTC pre-compiled library for Android. It reflects the recent WebRTC updates and supports functional UI components and extensions for Android and Jetpack Compose.

## Agenda
Since Google no more supports [WebRTC library for Android](https://webrtc.github.io/webrtc-org/native-code/android/) for many years (even JCenter has been shutdown, so the library is not available now), we decided to build our own pre-compiled WebRTC core library that reflects recent WebRTC commits with some improvements.

<a href="https://getstream.io/chat/sdk/compose?utm_source=Github&utm_medium=Github_Repo_Content_Ad&utm_content=Developer&utm_campaign=Github_Feb2023_Jaewoong_StreamWebRTCAndroid&utm_term=DevRelOss">
<img src="https://user-images.githubusercontent.com/24237865/138428440-b92e5fb7-89f8-41aa-96b1-71a5486c5849.png" align="right" width="12%"/>
</a>

## 🛥 Stream Chat and Voice & Video calling SDK

If you’re interested in adding powerful In-App Messaging to your app, check out the __[Compose Chat SDK for Messaging](https://getstream.io/chat/sdk/compose?utm_source=Github&utm_medium=Github_Repo_Content_Ad&utm_content=Developer&utm_campaign=Github_Feb2023_Jaewoong_StreamWebRTCAndroid&utm_term=DevRelOss)__! We're also planning to release voice & video calling SDK very soon! Check out the **[Video & Voice Calling API on Stream's Global Edge Network](https://getstream.io/video?utm_source=Github&utm_medium=Github_Repo_Content_Ad&utm_content=Developer&utm_campaign=Github_Feb2023_Jaewoong_StreamWebRTCAndroid&utm_term=DevRelOss)**, if you want early access to our SDK.


## Download
[![Maven Central](https://img.shields.io/maven-central/v/io.getstream/stream-webrtc-android.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.getstream%22%20AND%20a:%22stream-webrtc-android%22)

### Gradle

Add the below dependency to your **module**'s `build.gradle` file:

```gradle
dependencies {
    implementation "io.getstream:stream-webrtc-android:1.0.0"
}
```

## SNAPSHOT

<details>
 <summary>See how to import the snapshot</summary>

### Including the SNAPSHOT

Snapshots of the current development version of AvatarView are available, which track [the latest versions](https://oss.sonatype.org/content/repositories/snapshots/io/getstream/stream-webrtc-android/).

To import snapshot versions on your project, add the code snippet below on your gradle file.
```Gradle
repositories {
   maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
}
```

Next, add the below dependency to your **module**'s `build.gradle` file.
```gradle
dependencies {
    implementation "io.getstream:stream-webrtc-android:1.0.1-SNAPSHOT"
}
```

</details>

## Usages

Once you import this library, you can use all of the `org.webrtc` packge functions, such as `org.webrtc.PeerConnection` and `org.webrtc.VideoTrack`. For more information, you can check out the [API references for WebRTC packages](https://getstream.github.io/stream-webrtc-android/).

Here are the most commonly used APIs in the WebRTC library, and you can reference the documentation below:

- [PeerConnection](https://getstream.github.io/stream-webrtc-android/stream-webrtc-android/org.webrtc/-peer-connection/index.html?query=open%20class%20PeerConnection)
- [PeerConnectionFactory]()
- [EglBase](https://getstream.github.io/stream-webrtc-android/stream-webrtc-android/org.webrtc/-egl-base/index.html?query=interface%20EglBase)
- [VideoTrack](https://getstream.github.io/stream-webrtc-android/stream-webrtc-android/org.webrtc/-video-track/index.html?query=open%20class%20VideoTrack%20:%20MediaStreamTrack)
- [VideoSource](https://getstream.github.io/stream-webrtc-android/stream-webrtc-android/org.webrtc/-video-source/index.html?query=open%20class%20VideoSource%20:%20MediaSource)
- [AudioTrack](https://getstream.github.io/stream-webrtc-android/stream-webrtc-android/org.webrtc/-audio-track/index.html)
- [AudioSource](https://getstream.github.io/stream-webrtc-android/stream-webrtc-android/org.webrtc/-audio-source/index.html?query=open%20class%20AudioSource%20:%20MediaSource)
- [MediaStreamTrack](https://getstream.github.io/stream-webrtc-android/stream-webrtc-android/org.webrtc/-media-stream-track/index.html?query=open%20class%20MediaStreamTrack)
- [IceCandidate](https://getstream.github.io/stream-webrtc-android/stream-webrtc-android/org.webrtc/-ice-candidate/index.html?query=open%20class%20IceCandidate): Representation of a single ICE Candidate, mirroring `IceCandidateInterface` in the C++ API.
- [SessionDescription](https://getstream.github.io/stream-webrtc-android/stream-webrtc-android/org.webrtc/-session-description/index.html?query=open%20class%20SessionDescription): Description of an RFC 4566 Session. SDPs are passed as serialized Strings in Java-land and are materialized to SessionDescriptionInterface as appropriate in the JNI layer.
- [Camera2Capturer](https://getstream.github.io/stream-webrtc-android/stream-webrtc-android/org.webrtc/-camera2-capturer/index.html?query=open%20class%20Camera2Capturer%20:%20CameraCapturer)
- [Camera2Enumerator](https://getstream.github.io/stream-webrtc-android/stream-webrtc-android/org.webrtc/-camera2-enumerator/index.html?query=open%20class%20Camera2Enumerator%20:%20CameraEnumerator)
- [SurfaceViewRenderer](https://getstream.github.io/stream-webrtc-android/stream-webrtc-android/org.webrtc/-surface-view-renderer/index.html?query=open%20class%20SurfaceViewRenderer%20:%20SurfaceView,%20SurfaceHolder.Callback,%20VideoSink,%20RendererCommon.RendererEvents): Display the video stream on a SurfaceView.

If you want to learn more about building a video chat application for Android using WebRTC, check out the blog post below:

- **[Building a Video Chat App: WebRTC on Android (Part1)](https://getstream.io/blog/webrtc-on-android/)**

<img align="right" width="15%" src="https://user-images.githubusercontent.com/24237865/149445065-47c2506d-a738-4fb2-b4fb-eb6841b9e202.png" />

## WebRTC for UI Components

[![Maven Central](https://img.shields.io/maven-central/v/io.getstream/stream-webrtc-android-ui.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.getstream%22%20AND%20a:%22stream-webrtc-android-ui%22)

**Stream WebRTC Android** supports some uesful UI components for WebRTC, such as `VideoTextureViewRenderer`. First, add the dependency below to your **module's** `build.gradle` file:

```gradle
dependencies {
    implementation "io.getstream:stream-webrtc-android-ui:$version"
}
```

### VideoTextureViewRenderer

`VideoTextureViewRenderer` is a custom [TextureView](https://developer.android.com/reference/android/view/TextureView) that implements [VideoSink](https://getstream.github.io/stream-webrtc-android/stream-webrtc-android/org.webrtc/-video-sink/index.html?query=interface%20VideoSink) and [SurfaceTextureListener](https://developer.android.com/reference/kotlin/android/view/TextureView.SurfaceTextureListener.html). 

Usually, you can use [SurfaceViewRenderer](https://getstream.github.io/stream-webrtc-android/stream-webrtc-android/org.webrtc/-surface-view-renderer/index.html?query=open%20class%20SurfaceViewRenderer%20:%20SurfaceView,%20SurfaceHolder.Callback,%20VideoSink,%20RendererCommon.RendererEvents) to display real-time video streams on a layout if you need a simple video call screen without overlaying video frames over another one. However, it might not work well as you expect if you suppose to need to design a complex video call screen, such as one video call layout should overlay another video call layout, such as the example below:

![Screenshot](https://user-images.githubusercontent.com/24237865/218671884-d027ef03-1ccc-4d12-8153-adc2964034cc.png)

For this case, we'd recommend you use `VideoTextureViewRenderer` like the example below:

```xml
<io.getstream.webrtc.android.ui.VideoTextureViewRenderer
    android:id="@+id/participantVideoRenderer"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
 />
```

You can add or remove [VideoTrack](https://getstream.github.io/stream-webrtc-android/stream-webrtc-android/org.webrtc/-video-track/index.html?query=open%20class%20VideoTrack%20:%20MediaStreamTrack) like the below:

```kotlin
videoTrack.video.addSink(participantVideoRenderer)
videoTrack.video.removeSink(participantVideoRenderer)
```

<img align="right" width="15%" src="https://user-images.githubusercontent.com/24237865/149444862-961adb83-da2a-4179-9c27-37edb2f982f4.png">

## WebRTC for Jetpack Compose

[![Maven Central](https://img.shields.io/maven-central/v/io.getstream/stream-webrtc-android-compose.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.getstream%22%20AND%20a:%22stream-webrtc-android-compose%22)

**Stream WebRTC Android** supports some Jetpack Compose components for WebRTC, such as `VideoRenderer` and `FloatingVideoRenderer`. First, add the dependency below to your **module's** `build.gradle` file:

```gradle
dependencies {
    implementation "io.getstream:stream-webrtc-android-compose:$version"
}
```

### VideoRenderer

`VideoRenderer` is a composable function that renders a single video track in Jetpack Compose. 

```kotlin
VideoRenderer(
    videoTrack = remoteVideoTrack,
    modifier = Modifier.fillMaxSize()
    eglBaseContext = eglBaseContext,
    rendererEvents = rendererEvents
)
```

You can observe the rendering state changes by giving `RendererEvents` interface like the below:

```kotlin
val rendererEvents = object : RendererEvents {
      override fun onFirstFrameRendered() { .. }
      override fun onFrameResolutionChanged(videoWidth: Int, videoHeight: Int, rotation: Int) { .. }
}
```

### FloatingVideoRenderer

`FloatingVideoRenderer` represents a floating item that features a participant video, usually the local participant. You can use this composable function to overlay a single video track on another, and users can move the floating video track with user interactions.

You can use `FloatingVideoRenderer` with `VideoRenderer` like the example below:

```kotlin
var parentSize: IntSize by remember { mutableStateOf(IntSize(0, 0)) }

if (remoteVideoTrack != null) {
  VideoRenderer(
    videoTrack = remoteVideoTrack,
    modifier = Modifier
      .fillMaxSize()
      .onSizeChanged { parentSize = it },
    eglBaseContext = eglBaseContext,
    rendererEvents = rendererEvents
  )
}

if (localVideoTrack != null) {
  FloatingVideoRenderer(
    modifier = Modifier
      .size(width = 150.dp, height = 210.dp)
      .clip(RoundedCornerShape(16.dp))
      .align(Alignment.TopEnd),
    videoTrack = localVideoTrack,
    parentBounds = parentSize,
    paddingValues = PaddingValues(0.dp),
    eglBaseContext = eglBaseContexteglBaseContext,
    rendererEvents = rendererEvents
  )
}
```

<img align="right" width="90px" src="https://user-images.githubusercontent.com/24237865/178630165-76855349-ac04-4474-8bcf-8eb5f8c41095.png"/>

## WebRTC KTX

[![Maven Central](https://img.shields.io/maven-central/v/io.getstream/stream-webrtc-android-ktx.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.getstream%22%20AND%20a:%22stream-webrtc-android-ktx%22)

**Stream WebRTC Android** supports some useful extensions for WebRTC based on [Kotlin's Coroutines](https://kotlinlang.org/docs/coroutines-overview.html). First, add the dependency below to your **module's** `build.gradle` file:

```gradle
dependencies {
    implementation "io.getstream:stream-webrtc-android-ktx:$version"
}
```

### addRtcIceCandidate

`addRtcIceCandidate` is a suspend function that allows you to add a given `IceCandidate` to a `PeerConnection`. So you can add an `IceCandidate` to a `PeerConnection` as Coroutines-style, not callback-style.

```kotlin
pendingIceMutex.withLock {
    pendingIceCandidates.forEach { iceCandidate ->
    connection.addRtcIceCandidate(iceCandidate)
    }
    pendingIceCandidates.clear()
}
```

### createSessionDescription

You can create a `SessionDescription`, which delegates `SdpObserver` with Coroutines styles:

```kotlin
suspend fun createAnswer(): Result<SessionDescription> {
  return createSessionDescription { sdpObserver -> connection.createAnswer(sdpObserver, mediaConstraints) }
}
```

## Instructions for Setting Up Chromium Dev Tool

This is an instruction for setting up Chromium Dev Tool if you need to compile the WebRTC core library by yourself with this project.

[Chromium Dev Tools](https://commondatastorage.googleapis.com/chrome-infra-docs/flat/depot_tools/docs/html/depot_tools_tutorial.html#_setting_up)

- You need to set up depot tools to build & fetch Chromium codebase.

- You should fetch the chromium WebRTC repository from the Google's repository against HEAD commits.

<img width="449" alt="Screenshot 2023-02-08 at 11 47 14 AM" src="https://user-images.githubusercontent.com/24237865/218024381-7005bf74-cac9-4501-bbe8-e4258d3fa384.png">

> **Note**: Chromium WebRTC core libraries can be bulit only in Linux OS. Every step takes its own time based on the machine specs and internet speed, so make sure every step is completed without interruption.

You need to set up AWS instance (pre-requiests):

- Ubuntu 14.04 LTS (trusty with EoL April 2022)
- 8 GB memory ram
- At least 50 GB HDD/SSD storage

To compile the pre-built WebRTC library for Android, you must follow the steps below:

```
1. git clone https://chromium.googlesource.com/chromium/tools/depot_tools.git
    
2. export PATH="$PATH:${HOME}/depot_tools"
    
3. mkdir webrtc_android && cd webrtc_android
    
4. fetch --nohooks webrtc_android
        
5. gclient sync
    
6. cd src && ./build/install-build-deps.sh
    
7. git branch -r
    
8. git checkout origin/master
    
# To check you're in origin/master branch and check out to a specific branch if you want.
9. git branch

10. Replace Android sources & NDK/C/C++ files with this repository.

11. tools_webrtc/android/build_aar.py
```

To install all required dependencies for linux, a script is provided for Ubuntu, which is unfortunately only available after your first gclient sync and make sure your current directory is `webrtc_android/src/`:

```
cd src && ./build/install-build-deps.sh
```

You can see the available latest branches looks like the screenshoos below:

<img width="270" alt="Screenshot 2023-02-14 at 5 26 32 PM" src="https://user-images.githubusercontent.com/24237865/218680102-d7522dd5-1cf1-4c3b-ba61-463b75f5f714.png">


Now you can checkout to the latest branch which is `branch-heads/m79` or something, using this command:

```
git checkout branch-heads/m79
```

However, this project reflects the latest updates for WebRTC, so you must check out to the master branch like this:

```
8. git checkout origin/master
```

This will help you to resolve most of compilation issues. To get the details about your current branch you can simply use these commands:

```
9. git branch
```

### Using Manual Compilation:

This process will manually compile the source code for each particular CPU type. Manual Compiling involves these two steps:

1. Generate projects using GN.
2. Compile using Ninja.

This step will compile the library for Debug and Release modes of Development.

Ensure your current working directory is webrtc_android/src/ of your workspace. Then run:

```
11. gn gen out/Debug --args='target_os="android" target_cpu="arm"'
11. gn gen out/Release --args='is_debug=false is_component_build=false rtc_include_tests=false target_os="android" target_cpu="arm"'
```

You can specify a directory of your own choice instead of out/Debug, to enable managing multiple configurations in parallel.

- To build for ARM64: use target_cpu="arm64"
- To build for 32-bit x86: use target_cpu="x86"
- To build for 64-bit x64: use target_cpu="x64"

For compilation you can simply use these following commands for (out/Debug, out/Release):
```
11. ninja -C out/Debug
11. ninja -C out/Release
```

### Using AAR Build Tools:

This is the most simple process, which compiles the source code for all supported CPU types such as:

- arm64-v8a
- armeabi-v7a
- x86
- x86_64

After compiling the package, it includes all these native libraries and `.jar` library into `*.aar` file.

Make sure your current working directory is `webrtc_android/src/` of your workspace. Then run:

```
11. tools_webrtc/android/build_aar.py
```

This process will take some time based on your machine specs and internet speed, so here we go:

![image](https://user-images.githubusercontent.com/24237865/218025156-22fd6460-803a-4490-bbd5-48a1c4f1f452.png)

Now, if you look in the `webrtc_android/src/` directory, It turns out that you will end up with the compilation and building of `libwebrtc.aar`.

## Find this library useful? 💙

Support it by joining __[stargazers](https://github.com/getStream/stream-webrtc-android/stargazers)__ for this repository. ⭐️ <br>
Also, follow __[maintainers](https://github.com/skydoves)__ on GitHub for our next creations! 🤩

# License
```xml
Copyright 2023 Stream.IO, Inc. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
