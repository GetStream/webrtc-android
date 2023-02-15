/*
 *  Copyright 2013 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package org.webrtc;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Adopted from..
 * https://github.com/shiguredo-webrtc-build/webrtc-build/blob/master/patches/android_simulcast.patch
 */
public class SimulcastVideoEncoderFactory implements VideoEncoderFactory {
  VideoEncoderFactory primary;
  VideoEncoderFactory fallback;

  public SimulcastVideoEncoderFactory(VideoEncoderFactory primary, VideoEncoderFactory fallback) {
    this.primary = primary;
    this.fallback = fallback;
  }

  @Nullable
  public VideoEncoder createEncoder(VideoCodecInfo info) {
    return new SimulcastVideoEncoder(this.primary, this.fallback, info);
  }

  public VideoCodecInfo[] getSupportedCodecs() {
    List<VideoCodecInfo> codecs = new ArrayList();
    codecs.addAll(Arrays.asList(this.primary.getSupportedCodecs()));
    codecs.addAll(Arrays.asList(this.fallback.getSupportedCodecs()));
    return (VideoCodecInfo[]) codecs.toArray(new VideoCodecInfo[codecs.size()]);
  }
}
