/*
 *  Copyright 2024 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package io.getstream.webrtc;

/** AudioProcessing factory with lifecycle management and runtime control capabilities. */
public interface ManagedAudioProcessingFactory extends AudioProcessingFactory {
  /**
   * Destroys the native AudioProcessing instance.
   */
  public void destroyNative();

  /**
   * Checks if the AudioProcessing is enabled.
   * @return true if enabled, false otherwise.
   */
  public boolean isEnabled();

  /**
   * Sets the enabled state of the AudioProcessing.
   * @param enabled The desired enabled state.
   */
  public void setEnabled(boolean enabled);
}