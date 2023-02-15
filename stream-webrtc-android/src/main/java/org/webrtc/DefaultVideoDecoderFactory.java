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

/**
 * Helper class that combines HW and SW decoders.
 */
public class DefaultVideoDecoderFactory implements VideoDecoderFactory {
  private final VideoDecoderFactory hardwareVideoDecoderFactory;
  private final VideoDecoderFactory softwareVideoDecoderFactory = new SoftwareVideoDecoderFactory();
  @Nullable
  private final VideoDecoderFactory platformSoftwareVideoDecoderFactory;

  public DefaultVideoDecoderFactory(@Nullable EglBase.Context eglContext) {
    this.hardwareVideoDecoderFactory = new HardwareVideoDecoderFactory(eglContext);
    this.platformSoftwareVideoDecoderFactory = new PlatformSoftwareVideoDecoderFactory(eglContext);
  }

  DefaultVideoDecoderFactory(VideoDecoderFactory hardwareVideoDecoderFactory) {
    this.hardwareVideoDecoderFactory = hardwareVideoDecoderFactory;
    this.platformSoftwareVideoDecoderFactory = null;
  }

  @Nullable
  public VideoDecoder createDecoder(VideoCodecInfo codecType) {
    VideoDecoder softwareDecoder = this.softwareVideoDecoderFactory.createDecoder(codecType);
    VideoDecoder hardwareDecoder = this.hardwareVideoDecoderFactory.createDecoder(codecType);
    if (softwareDecoder == null && this.platformSoftwareVideoDecoderFactory != null) {
      softwareDecoder = this.platformSoftwareVideoDecoderFactory.createDecoder(codecType);
    }

    if (hardwareDecoder != null && softwareDecoder != null) {
      return new VideoDecoderFallback(softwareDecoder, hardwareDecoder);
    } else {
      return hardwareDecoder != null ? hardwareDecoder : softwareDecoder;
    }
  }

  public VideoCodecInfo[] getSupportedCodecs() {
    LinkedHashSet<VideoCodecInfo> supportedCodecInfos = new LinkedHashSet();
    supportedCodecInfos.addAll(Arrays.asList(this.softwareVideoDecoderFactory.getSupportedCodecs()));
    supportedCodecInfos.addAll(Arrays.asList(this.hardwareVideoDecoderFactory.getSupportedCodecs()));
    if (this.platformSoftwareVideoDecoderFactory != null) {
      supportedCodecInfos.addAll(Arrays.asList(this.platformSoftwareVideoDecoderFactory.getSupportedCodecs()));
    }

    return (VideoCodecInfo[])supportedCodecInfos.toArray(new VideoCodecInfo[supportedCodecInfos.size()]);
  }
}
