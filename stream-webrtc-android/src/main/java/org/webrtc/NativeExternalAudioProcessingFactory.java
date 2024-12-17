package org.webrtc;

public class NativeExternalAudioProcessingFactory implements AudioProcessingFactory {

  private final String libname;

  public NativeExternalAudioProcessingFactory(String libname) {
    if (libname == null) {
      throw new NullPointerException("libname must not be null.");
    }
    if (libname.isEmpty()) {
      throw new IllegalArgumentException("libname must not be empty.");
    }
    this.libname = libname;
  }

  @Override
  public long createNative() {
    return nativeCreateAudioProcessingModule(libname);
  }

  public void destroyNative() {
    nativeDestroyAudioProcessingModule();
  }

  private static native long nativeCreateAudioProcessingModule(String libname);


  private static native void nativeDestroyAudioProcessingModule();

}