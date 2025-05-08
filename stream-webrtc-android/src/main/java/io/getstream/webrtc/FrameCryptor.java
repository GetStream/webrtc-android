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

import androidx.annotation.Nullable;

public class FrameCryptor {
  public enum FrameCryptionState {
    NEW,
    OK,
    ENCRYPTIONFAILED,
    DECRYPTIONFAILED,
    MISSINGKEY,
    KEYRATCHETED,
    INTERNALERROR;

    @CalledByNative("FrameCryptionState")
    static FrameCryptionState fromNativeIndex(int nativeIndex) {
      return values()[nativeIndex];
    }
  }

  public static interface Observer {
    @CalledByNative("Observer")
    void onFrameCryptionStateChanged(String participantId, FrameCryptionState newState);
  }

  private long nativeFrameCryptor;
  private long observerPtr;

  public long getNativeFrameCryptor() {
    return nativeFrameCryptor;
  }

  @CalledByNative
  public FrameCryptor(long nativeFrameCryptor) {
    this.nativeFrameCryptor = nativeFrameCryptor;
    this.observerPtr = 0;
  }

  public void setEnabled(boolean enabled) {
    checkFrameCryptorExists();
    nativeSetEnabled(nativeFrameCryptor, enabled);
  }

  public boolean isEnabled() {
    checkFrameCryptorExists();
    return nativeIsEnabled(nativeFrameCryptor);
  }

  public int getKeyIndex() {
    checkFrameCryptorExists();
    return nativeGetKeyIndex(nativeFrameCryptor);
  }

  public void setKeyIndex(int index) {
    checkFrameCryptorExists();
    nativeSetKeyIndex(nativeFrameCryptor, index);
  }

  public void dispose() {
    checkFrameCryptorExists();
    nativeUnSetObserver(nativeFrameCryptor);
    JniCommon.nativeReleaseRef(nativeFrameCryptor);
    nativeFrameCryptor = 0;
    if (observerPtr != 0) {
      JniCommon.nativeReleaseRef(observerPtr);
      observerPtr = 0;
    }
  }

  public void setObserver(@Nullable Observer observer) {
    checkFrameCryptorExists();
    long newPtr = nativeSetObserver(nativeFrameCryptor, observer);
    if (observerPtr != 0) {
      JniCommon.nativeReleaseRef(observerPtr);
      observerPtr = 0;
    }
    newPtr = observerPtr;
  }

  private void checkFrameCryptorExists() {
    if (nativeFrameCryptor == 0) {
      throw new IllegalStateException("FrameCryptor has been disposed.");
    }
  }

  private static native void nativeSetEnabled(long frameCryptorPointer, boolean enabled);
  private static native boolean nativeIsEnabled(long frameCryptorPointer);
  private static native void nativeSetKeyIndex(long frameCryptorPointer, int index);
  private static native int nativeGetKeyIndex(long frameCryptorPointer);
  private static native long nativeSetObserver(long frameCryptorPointer, Observer observer);
  private static native void nativeUnSetObserver(long frameCryptorPointer);
}
