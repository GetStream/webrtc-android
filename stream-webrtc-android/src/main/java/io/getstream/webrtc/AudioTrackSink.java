/*
 *  Copyright 2023 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package io.getstream.webrtc;

import java.nio.ByteBuffer;

/**
 * Java version of rtc::AudioTrackSinkInterface.
 */
public interface AudioTrackSink {
  /**
   * Implementations should copy the audio data into a local copy if they wish
   * to use the data after this function returns.
   */
  @CalledByNative 
  void onData(ByteBuffer audioData, int bitsPerSample, int sampleRate,
      int numberOfChannels, int numberOfFrames, 
      long absoluteCaptureTimestampMs);
}
