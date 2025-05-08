/*
 *  Copyright 2017 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package io.getstream.webrtc;

import androidx.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class SoftwareVideoEncoderFactory implements VideoEncoderFactory {
  private static final String TAG = "SoftwareVideoEncoderFactory";

  private final long nativeFactory;

  public SoftwareVideoEncoderFactory() {
    this.nativeFactory = nativeCreateFactory();
  }

  @Nullable
  @Override
  public VideoEncoder createEncoder(VideoCodecInfo info) {
    if (!nativeIsSupported(nativeFactory, info)) {
      Logging.w(TAG, "Trying to create encoder for unsupported format. " + info);
      return null;
    }

    return new WrappedNativeVideoEncoder() {
      @Override
      public long createNative(long webrtcEnvRef) {
        return nativeCreate(nativeFactory, webrtcEnvRef, info);
      }

      @Override
      public boolean isHardwareEncoder() {
        return false;
      }
    };
  }

  @Override
  public VideoCodecInfo[] getSupportedCodecs() {
    return nativeGetSupportedCodecs(nativeFactory).toArray(new VideoCodecInfo[0]);
  }

  private static native long nativeCreateFactory();

  private static native boolean nativeIsSupported(long factory, VideoCodecInfo info);

  private static native long nativeCreate(long factory, long webrtcEnvRef, VideoCodecInfo info);

  private static native List<VideoCodecInfo> nativeGetSupportedCodecs(long factory);
}
