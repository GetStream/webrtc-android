/*
 *  Copyright 2015 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package org.webrtc;

import androidx.annotation.Nullable;
import java.util.List;

/** Java wrapper for a C++ RtpSenderInterface. */
public class RtpSender {
  private long nativeRtpSender;
  @Nullable
  private MediaStreamTrack cachedTrack;
  private boolean ownsTrack = true;
  @Nullable
  private final DtmfSender dtmfSender;

  @CalledByNative
  public RtpSender(long nativeRtpSender) {
    this.nativeRtpSender = nativeRtpSender;
    long nativeTrack = nativeGetTrack(nativeRtpSender);
    this.cachedTrack = MediaStreamTrack.createMediaStreamTrack(nativeTrack);
    long nativeDtmfSender = nativeGetDtmfSender(nativeRtpSender);
    this.dtmfSender = nativeDtmfSender != 0L ? new DtmfSender(nativeDtmfSender) : null;
  }

  /**
   * Starts sending a new track, without requiring additional SDP negotiation.
   * <p>
   * Note: This is equivalent to replaceTrack in the official WebRTC API. It
   * was just implemented before the standards group settled on a name.
   *
   * @param takeOwnership If true, the RtpSender takes ownership of the track
   *                      from the caller, and will auto-dispose of it when no
   *                      longer needed. `takeOwnership` should only be used if
   *                      the caller owns the track; it is not appropriate when
   *                      the track is owned by, for example, another RtpSender
   *                      or a MediaStream.
   * @return              true on success and false on failure.
   */
  public boolean setTrack(@Nullable MediaStreamTrack track, boolean takeOwnership) {
    this.checkRtpSenderExists();
    if (!nativeSetTrack(this.nativeRtpSender, track == null ? 0L : track.getNativeMediaStreamTrack())) {
      return false;
    } else {
      if (this.cachedTrack != null && this.ownsTrack) {
        this.cachedTrack.dispose();
      }

      this.cachedTrack = track;
      this.ownsTrack = takeOwnership;
      return true;
    }
  }

  @Nullable
  public MediaStreamTrack track() {
    return this.cachedTrack;
  }

  public void setStreams(List<String> streamIds) {
    this.checkRtpSenderExists();
    nativeSetStreams(this.nativeRtpSender, streamIds);
  }

  public List<String> getStreams() {
    this.checkRtpSenderExists();
    return nativeGetStreams(this.nativeRtpSender);
  }

  public boolean setParameters(RtpParameters parameters) {
    this.checkRtpSenderExists();
    return nativeSetParameters(this.nativeRtpSender, parameters);
  }

  public RtpParameters getParameters() {
    this.checkRtpSenderExists();
    return nativeGetParameters(this.nativeRtpSender);
  }

  public String id() {
    this.checkRtpSenderExists();
    return nativeGetId(this.nativeRtpSender);
  }

  @Nullable
  public DtmfSender dtmf() {
    return this.dtmfSender;
  }

  public void setFrameEncryptor(FrameEncryptor frameEncryptor) {
    this.checkRtpSenderExists();
    nativeSetFrameEncryptor(this.nativeRtpSender, frameEncryptor.getNativeFrameEncryptor());
  }

  public void dispose() {
    this.checkRtpSenderExists();
    if (this.dtmfSender != null) {
      this.dtmfSender.dispose();
    }

    if (this.cachedTrack != null && this.ownsTrack) {
      this.cachedTrack.dispose();
    }

    JniCommon.nativeReleaseRef(this.nativeRtpSender);
    this.nativeRtpSender = 0L;
  }

  long getNativeRtpSender() {
    this.checkRtpSenderExists();
    return this.nativeRtpSender;
  }

  private void checkRtpSenderExists() {
    if (this.nativeRtpSender == 0L) {
      throw new IllegalStateException("RtpSender has been disposed.");
    }
  }

  private static native boolean nativeSetTrack(long rtpSender, long nativeTrack);

  // This should increment the reference count of the track.
  // Will be released in dispose() or setTrack().
  private static native long nativeGetTrack(long rtpSender);

  private static native void nativeSetStreams(long rtpSender, List<String> streamIds);

  private static native List<String> nativeGetStreams(long rtpSender);

  // This should increment the reference count of the DTMF sender.
  // Will be released in dispose().
  private static native long nativeGetDtmfSender(long rtpSender);

  private static native boolean nativeSetParameters(long rtpSender, RtpParameters parameters);

  private static native RtpParameters nativeGetParameters(long rtpSender);

  private static native String nativeGetId(long rtpSender);

  private static native void nativeSetFrameEncryptor(long rtpSender, long nativeFrameEncryptor);
};
