package org.webrtc;

public class LibaomAv1Decoder extends WrappedNativeVideoDecoder {
  public LibaomAv1Decoder() {
  }

  public long createNativeVideoDecoder() {
    return nativeCreateDecoder();
  }

  static native long nativeCreateDecoder();

  static native boolean nativeIsSupported();
}
