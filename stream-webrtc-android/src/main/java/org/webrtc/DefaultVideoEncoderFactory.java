/*
 *  Copyright 2017 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package org.webrtc;

import androidx.annotation.Nullable;
import java.util.Arrays;
import java.util.LinkedHashSet;

/** Helper class that combines HW and SW encoders. */
public class DefaultVideoEncoderFactory implements VideoEncoderFactory {
  private final VideoEncoderFactory hardwareVideoEncoderFactory;
  private final VideoEncoderFactory softwareVideoEncoderFactory = new SoftwareVideoEncoderFactory();

  public DefaultVideoEncoderFactory(EglBase.Context eglContext, boolean enableIntelVp8Encoder, boolean enableH264HighProfile) {
    this.hardwareVideoEncoderFactory = new HardwareVideoEncoderFactory(eglContext, enableIntelVp8Encoder, enableH264HighProfile);
  }

  DefaultVideoEncoderFactory(VideoEncoderFactory hardwareVideoEncoderFactory) {
    this.hardwareVideoEncoderFactory = hardwareVideoEncoderFactory;
  }

  @Nullable
  public VideoEncoder createEncoder(VideoCodecInfo info) {
    VideoEncoder softwareEncoder = this.softwareVideoEncoderFactory.createEncoder(info);
    VideoEncoder hardwareEncoder = this.hardwareVideoEncoderFactory.createEncoder(info);
    if (hardwareEncoder != null && softwareEncoder != null) {
      return new VideoEncoderFallback(softwareEncoder, hardwareEncoder);
    } else {
      return hardwareEncoder != null ? hardwareEncoder : softwareEncoder;
    }
  }

  public VideoCodecInfo[] getSupportedCodecs() {
    LinkedHashSet<VideoCodecInfo> supportedCodecInfos = new LinkedHashSet();
    supportedCodecInfos.addAll(Arrays.asList(this.softwareVideoEncoderFactory.getSupportedCodecs()));
    supportedCodecInfos.addAll(Arrays.asList(this.hardwareVideoEncoderFactory.getSupportedCodecs()));
    return (VideoCodecInfo[])supportedCodecInfos.toArray(new VideoCodecInfo[supportedCodecInfos.size()]);
  }
}
