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

import java.util.ArrayList;

public class FrameCryptorKeyProvider {
  private long nativeKeyProvider;

  @CalledByNative
  public FrameCryptorKeyProvider(long nativeKeyProvider) {
    this.nativeKeyProvider = nativeKeyProvider;
  }

  public long getNativeKeyProvider() {
    return nativeKeyProvider;
  }

  public boolean setSharedKey(int index, byte[] key) {
    checkKeyProviderExists();
    return nativeSetSharedKey(nativeKeyProvider,index, key);
  }

  public byte[] ratchetSharedKey(int index) {
    checkKeyProviderExists();
    return nativeRatchetSharedKey(nativeKeyProvider, index);
  }

  public byte[] exportSharedKey(int index) {
    checkKeyProviderExists();
    return nativeExportSharedKey(nativeKeyProvider, index);
  }

  public boolean setKey(String participantId, int index, byte[] key) {
    checkKeyProviderExists();
    return nativeSetKey(nativeKeyProvider, participantId, index, key);
  }

  public byte[] ratchetKey(String participantId, int index) {
    checkKeyProviderExists();
    return nativeRatchetKey(nativeKeyProvider, participantId, index);
  }

  public byte[] exportKey(String participantId, int index) {
    checkKeyProviderExists();
    return nativeExportKey(nativeKeyProvider, participantId, index);
  }

  public void setSifTrailer(byte[] sifTrailer) {
    checkKeyProviderExists();
    nativeSetSifTrailer(nativeKeyProvider, sifTrailer);
  }

  public void dispose() {
    checkKeyProviderExists();
    JniCommon.nativeReleaseRef(nativeKeyProvider);
    nativeKeyProvider = 0;
  }

  private void checkKeyProviderExists() {
    if (nativeKeyProvider == 0) {
      throw new IllegalStateException("FrameCryptorKeyProvider has been disposed.");
    }
  }
  private static native boolean nativeSetSharedKey(
      long keyProviderPointer, int index, byte[] key);
  private static native byte[] nativeRatchetSharedKey(
      long keyProviderPointer, int index);
  private static native byte[] nativeExportSharedKey(
      long keyProviderPointer, int index);
  private static native boolean nativeSetKey(
      long keyProviderPointer, String participantId, int index, byte[] key);
  private static native byte[] nativeRatchetKey(
      long keyProviderPointer, String participantId, int index);
  private static native byte[] nativeExportKey(
      long keyProviderPointer, String participantId, int index);
  private static native void nativeSetSifTrailer(
      long keyProviderPointer, byte[] sifTrailer);
}