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

import org.webrtc.RendererCommon

/**
 * Types of video scaling:
 * SCALE_ASPECT_FIT - video frame is scaled to fit the size of the view by
 * maintaining the aspect ratio (black borders may be displayed).
 *
 * SCALE_ASPECT_FILL - video frame is scaled to fill the size of the view by
 * maintaining the aspect ratio. Some portion of the video frame may be clipped.
 *
 * SCALE_ASPECT_BALANCED - Compromise between FIT and FILL. Video frame will fill as much as
 * possible of the view while maintaining aspect ratio, under the constraint that at least
 * `BALANCED_VISIBLE_FRACTION` of the frame content will be shown.
 */
public enum class VideoScalingType {
  SCALE_ASPECT_FIT,
  SCALE_ASPECT_FILL,
  SCALE_ASPECT_BALANCED;

  public companion object {

    @Deprecated(
      message = "Use SCALE_ASPECT_FILL instead",
      replaceWith = ReplaceWith("VideoScalingType.SCALE_ASPECT_FILL")
    )
    @JvmField
    public val SCAPE_ASPECT_FILL: VideoScalingType = SCALE_ASPECT_FILL

    internal fun VideoScalingType.toCommonScalingType(): RendererCommon.ScalingType {
      return when (this) {
        SCALE_ASPECT_FIT -> RendererCommon.ScalingType.SCALE_ASPECT_FIT
        SCALE_ASPECT_FILL -> RendererCommon.ScalingType.SCALE_ASPECT_FILL
        SCALE_ASPECT_BALANCED -> RendererCommon.ScalingType.SCALE_ASPECT_BALANCED
      }
    }
  }
}
