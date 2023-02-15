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

/**
 * Adopted from..
 * https://github.com/shiguredo-webrtc-build/webrtc-build/blob/master/patches/android_simulcast.patch
 */
public class SimulcastVideoEncoder extends WrappedNativeVideoEncoder {
  VideoEncoderFactory primary;
  VideoEncoderFactory fallback;
  VideoCodecInfo info;

  static native long nativeCreateEncoder(VideoEncoderFactory var0, VideoEncoderFactory var1, VideoCodecInfo var2);

  public SimulcastVideoEncoder(VideoEncoderFactory primary, VideoEncoderFactory fallback, VideoCodecInfo info) {
    this.primary = primary;
    this.fallback = fallback;
    this.info = info;
  }

  public long createNativeVideoEncoder() {
    return nativeCreateEncoder(this.primary, this.fallback, this.info);
  }

  public boolean isHardwareEncoder() {
    return false;
  }
}
