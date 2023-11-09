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

package org.webrtc

/**
 * The main difference with the standard [DefaultAlignedVideoEncoderFactory] is that this fixes
 * issues with resolutions that are not aligned (e.g. VP8 requires 16x16 alignment). You can
 * set the alignment by setting [resolutionAdjustment]. Internally the resolution during streaming
 * will be cropped to comply with the adjustment. Fallback behaviour is the same as with the
 * standard [DefaultVideoEncoderFactory] and it will use the SW encoder if HW fails
 * or is not available.
 *
 * Original source: https://github.com/shiguredo/sora-android-sdk/blob/3cc88e806ab2f2327bf3042072
 * e98d6da9df4408/sora-android-sdk/src/main/kotlin/jp/shiguredo/sora/sdk/codec/SimulcastVideoEnco
 * derFactoryWrapper.kt#L18
 */
class DefaultAlignedVideoEncoderFactory(
  eglContext: EglBase.Context?,
  enableIntelVp8Encoder: Boolean = true,
  enableH264HighProfile: Boolean = false,
  resolutionAdjustment: ResolutionAdjustment,
) : VideoEncoderFactory {
  private val hardwareVideoEncoderFactory: VideoEncoderFactory
  private val softwareVideoEncoderFactory: VideoEncoderFactory = SoftwareVideoEncoderFactory()

  init {
    val defaultFactory =
      HardwareVideoEncoderFactory(eglContext, enableIntelVp8Encoder, enableH264HighProfile)

    hardwareVideoEncoderFactory = if (resolutionAdjustment == ResolutionAdjustment.NONE) {
      defaultFactory
    } else {
      HardwareVideoEncoderWrapperFactory(defaultFactory, resolutionAdjustment.value)
    }
  }

  override fun createEncoder(info: VideoCodecInfo): VideoEncoder? {
    val softwareEncoder: VideoEncoder? = softwareVideoEncoderFactory.createEncoder(info)
    val hardwareEncoder: VideoEncoder? = hardwareVideoEncoderFactory.createEncoder(info)
    if (hardwareEncoder != null && softwareEncoder != null) {
      return VideoEncoderFallback(softwareEncoder, hardwareEncoder)
    }
    return hardwareEncoder ?: softwareEncoder
  }

  override fun getSupportedCodecs(): Array<VideoCodecInfo> {
    val supportedCodecInfos = LinkedHashSet<VideoCodecInfo>()
    supportedCodecInfos.addAll(listOf(*softwareVideoEncoderFactory.supportedCodecs))
    supportedCodecInfos.addAll(listOf(*hardwareVideoEncoderFactory.supportedCodecs))
    return supportedCodecInfos.toTypedArray()
  }
}
