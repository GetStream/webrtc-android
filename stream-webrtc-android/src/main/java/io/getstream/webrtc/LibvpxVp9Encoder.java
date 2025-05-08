/*
 *  Copyright (c) 2017 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package io.getstream.webrtc;
import java.util.List;

public class LibvpxVp9Encoder extends WrappedNativeVideoEncoder {
  @Override
  public long createNative(long webrtcEnvRef) {
    return nativeCreate(webrtcEnvRef);
  }

  static native long nativeCreate(long webrtcEnvRef);

  @Override
  public boolean isHardwareEncoder() {
    return false;
  }

  static native boolean nativeIsSupported();

  static List<String> scalabilityModes() {
    return nativeGetSupportedScalabilityModes();
  }

  static native List<String> nativeGetSupportedScalabilityModes();
}
