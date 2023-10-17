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

package io.getstream.webrtc.sample.compose.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import io.getstream.webrtc.android.ui.VideoTextureViewRenderer
import io.getstream.webrtc.sample.compose.webrtc.sessions.LocalWebRtcSessionManager
import org.webrtc.RendererCommon
import org.webrtc.VideoTrack

/**
 * Renders a single video track based on the call state.
 *
 * @param videoTrack The track containing the video stream for a given participant.
 * @param modifier Modifier for styling.
 */
@Composable
fun VideoRendererasd(
  videoTrack: VideoTrack,
  modifier: Modifier = Modifier,
) {
  val trackState: MutableState<VideoTrack?> = remember { mutableStateOf(null) }
  var view: VideoTextureViewRenderer? by remember { mutableStateOf(null) }

  DisposableEffect(videoTrack) {
    onDispose {
      cleanTrack(view, trackState)
    }
  }

  val sessionManager = LocalWebRtcSessionManager.current
  AndroidView(
    factory = { context ->
      VideoTextureViewRenderer(context).apply {
        init(
          sessionManager.peerConnectionFactory.eglBaseContext,
          object : RendererCommon.RendererEvents {
            override fun onFirstFrameRendered() = Unit

            override fun onFrameResolutionChanged(p0: Int, p1: Int, p2: Int) = Unit
          },
        )
        setupVideo(trackState, videoTrack, this)
        view = this
      }
    },
    update = { v -> setupVideo(trackState, videoTrack, v) },
    modifier = modifier,
  )
}

private fun cleanTrack(
  view: VideoTextureViewRenderer?,
  trackState: MutableState<VideoTrack?>,
) {
  view?.let { trackState.value?.removeSink(it) }
  trackState.value = null
}

private fun setupVideo(
  trackState: MutableState<VideoTrack?>,
  track: VideoTrack,
  renderer: VideoTextureViewRenderer,
) {
  if (trackState.value == track) {
    return
  }

  cleanTrack(renderer, trackState)

  trackState.value = track
  track.addSink(renderer)
}
