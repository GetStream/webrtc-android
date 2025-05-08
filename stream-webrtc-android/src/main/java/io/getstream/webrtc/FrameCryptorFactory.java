/*
 * Copyright 2022 LiveKit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.getstream.webrtc;

public class FrameCryptorFactory {
  public static FrameCryptorKeyProvider createFrameCryptorKeyProvider(
      boolean sharedKey, byte[] ratchetSalt, int ratchetWindowSize, byte[] uncryptedMagicBytes, int failureTolerance, int keyRingSize, boolean discardFrameWhenCryptorNotReady) {
    return nativeCreateFrameCryptorKeyProvider(sharedKey, ratchetSalt, ratchetWindowSize, uncryptedMagicBytes, failureTolerance, keyRingSize, discardFrameWhenCryptorNotReady);
  }

  public static FrameCryptor createFrameCryptorForRtpSender(PeerConnectionFactory factory, RtpSender rtpSender,
      String participantId, FrameCryptorAlgorithm algorithm, FrameCryptorKeyProvider keyProvider) {
    return nativeCreateFrameCryptorForRtpSender(factory.getNativeOwnedFactoryAndThreads(),rtpSender.getNativeRtpSender(), participantId,
        algorithm.ordinal(), keyProvider.getNativeKeyProvider());
  }

  public static FrameCryptor createFrameCryptorForRtpReceiver(PeerConnectionFactory factory, RtpReceiver rtpReceiver,
      String participantId, FrameCryptorAlgorithm algorithm, FrameCryptorKeyProvider keyProvider) {
    return nativeCreateFrameCryptorForRtpReceiver(factory.getNativeOwnedFactoryAndThreads(), rtpReceiver.getNativeRtpReceiver(), participantId,
        algorithm.ordinal(), keyProvider.getNativeKeyProvider());
  }

  private static native FrameCryptor nativeCreateFrameCryptorForRtpSender(long factory,
      long rtpSender, String participantId, int algorithm, long nativeFrameCryptorKeyProvider);
  private static native FrameCryptor nativeCreateFrameCryptorForRtpReceiver(long factory,
      long rtpReceiver, String participantId, int algorithm, long nativeFrameCryptorKeyProvider);

  private static native FrameCryptorKeyProvider nativeCreateFrameCryptorKeyProvider(
      boolean sharedKey, byte[] ratchetSalt, int ratchetWindowSize, byte[] uncryptedMagicBytes, int failureTolerance, int keyRingSize, boolean discardFrameWhenCryptorNotReady);
}
