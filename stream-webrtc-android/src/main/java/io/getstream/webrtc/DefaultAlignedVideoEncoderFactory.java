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
package io.getstream.webrtc;

import java.util.Arrays;
import java.util.LinkedHashSet;

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
public class DefaultAlignedVideoEncoderFactory implements VideoEncoderFactory {
  private final VideoEncoderFactory hardwareVideoEncoderFactory;
  private final VideoEncoderFactory softwareVideoEncoderFactory;

  public DefaultAlignedVideoEncoderFactory(
    EglBase.Context eglContext,
  boolean enableIntelVp8Encoder,
  boolean enableH264HighProfile,
  ResolutionAdjustment resolutionAdjustment
  ) {
    HardwareVideoEncoderFactory defaultFactory =
    new HardwareVideoEncoderFactory(eglContext, enableIntelVp8Encoder, enableH264HighProfile);
    hardwareVideoEncoderFactory = (resolutionAdjustment == ResolutionAdjustment.NONE) ?
    defaultFactory :
    new HardwareVideoEncoderWrapperFactory(defaultFactory, resolutionAdjustment.getValue());
    softwareVideoEncoderFactory = new SoftwareVideoEncoderFactory();
  }

  @Override
  public VideoEncoder createEncoder(VideoCodecInfo info) {
    VideoEncoder softwareEncoder = softwareVideoEncoderFactory.createEncoder(info);
    VideoEncoder hardwareEncoder = hardwareVideoEncoderFactory.createEncoder(info);
    if (hardwareEncoder != null && softwareEncoder != null) {
      return new VideoEncoderFallback(softwareEncoder, hardwareEncoder);
    }
    return hardwareEncoder != null ? hardwareEncoder : softwareEncoder;
  }

  @Override
  public VideoCodecInfo[] getSupportedCodecs() {
    LinkedHashSet<VideoCodecInfo> supportedCodecInfos = new LinkedHashSet<>();
    supportedCodecInfos.addAll(Arrays.asList(softwareVideoEncoderFactory.getSupportedCodecs()));
    supportedCodecInfos.addAll(Arrays.asList(hardwareVideoEncoderFactory.getSupportedCodecs()));
    return supportedCodecInfos.toArray(new VideoCodecInfo[0]);
  }
}

