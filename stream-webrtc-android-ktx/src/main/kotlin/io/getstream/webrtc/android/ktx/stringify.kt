/*
 * Copyright (c) 2014-2023 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.getstream.webrtc.android.ktx

import org.webrtc.IceCandidateErrorEvent
import org.webrtc.MediaStreamTrack
import org.webrtc.SessionDescription
import org.webrtc.audio.JavaAudioDeviceModule

public fun SessionDescription.stringify(): String =
  "SessionDescription(type=$type, description=$description)"

public fun MediaStreamTrack.stringify(): String {
  return "MediaStreamTrack(id=${id()}, kind=${kind()}, enabled: ${enabled()}, state=${state()})"
}

public fun IceCandidateErrorEvent.stringify(): String {
  return "IceCandidateErrorEvent(errorCode=$errorCode, $errorText, address=$address, port=$port, url=$url)"
}

public fun JavaAudioDeviceModule.AudioSamples.stringify(): String {
  return "AudioSamples(audioFormat=$audioFormat, channelCount=$channelCount" +
    ", sampleRate=$sampleRate, data.size=${data.size})"
}
