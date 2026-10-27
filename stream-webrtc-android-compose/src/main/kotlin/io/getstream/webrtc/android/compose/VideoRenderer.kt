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

package io.getstream.webrtc.android.compose

import android.view.ViewGroup.LayoutParams
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.viewinterop.AndroidView
import io.getstream.webrtc.android.compose.VideoScalingType.Companion.toCommonScalingType
import io.getstream.webrtc.android.ui.VideoTextureViewRenderer
import org.webrtc.EglBase.Context
import org.webrtc.RendererCommon.RendererEvents
import org.webrtc.VideoTrack

/**
 * Renders a single video track based on the call state.
 *
 * @param videoTrack The track containing the video stream for a given participant.
 * @param modifier Modifier for styling.
 */
@Composable
public fun VideoRenderer(
  modifier: Modifier = Modifier,
  videoTrack: VideoTrack,
  eglBaseContext: Context,
  videoScalingType: VideoScalingType = VideoScalingType.SCALE_ASPECT_BALANCED,
  onTextureViewCreated: (VideoTextureViewRenderer) -> Unit = { },
  rendererEvents: RendererEvents,
) {
  val trackState: MutableState<VideoTrack?> = remember { mutableStateOf(null) }
  var view: VideoTextureViewRenderer? by remember { mutableStateOf(null) }
  val currentRendererEvents by rememberUpdatedState(rendererEvents)
  val forwardingRendererEvents = remember {
    object : RendererEvents {
      override fun onFirstFrameRendered() {
        currentRendererEvents.onFirstFrameRendered()
      }

      override fun onFrameResolutionChanged(videoWidth: Int, videoHeight: Int, rotation: Int) {
        currentRendererEvents.onFrameResolutionChanged(videoWidth, videoHeight, rotation)
      }
    }
  }

  DisposableEffect(videoTrack) {
    onDispose {
      cleanTrack(view, trackState)
    }
  }

  AndroidView(
    factory = { context ->
      VideoTextureViewRenderer(context).apply {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        init(eglBaseContext, forwardingRendererEvents)
        setScalingType(scalingType = videoScalingType.toCommonScalingType())
        setupVideo(trackState, videoTrack, this)
        onTextureViewCreated.invoke(this)
        view = this
      }
    },
    update = { v ->
      v.setScalingType(scalingType = videoScalingType.toCommonScalingType())
      setupVideo(trackState, videoTrack, v)
    },
    modifier = modifier.layout { measurable, constraints ->
      val placeable = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
      val width = constraints.constrainWidth(placeable.width)
      val height = constraints.constrainHeight(placeable.height)
      layout(width, height) {
        placeable.placeRelative((width - placeable.width) / 2, (height - placeable.height) / 2)
      }
    },
  )
}

private fun cleanTrack(view: VideoTextureViewRenderer?, trackState: MutableState<VideoTrack?>) {
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
