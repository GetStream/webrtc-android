//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.webrtc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RtpTransceiver {
  private long nativeRtpTransceiver;
  private RtpSender cachedSender;
  private RtpReceiver cachedReceiver;

  @CalledByNative
  protected RtpTransceiver(long nativeRtpTransceiver) {
    this.nativeRtpTransceiver = nativeRtpTransceiver;
    this.cachedSender = nativeGetSender(nativeRtpTransceiver);
    this.cachedReceiver = nativeGetReceiver(nativeRtpTransceiver);
  }

  public MediaStreamTrack.MediaType getMediaType() {
    this.checkRtpTransceiverExists();
    return nativeGetMediaType(this.nativeRtpTransceiver);
  }

  public String getMid() {
    this.checkRtpTransceiverExists();
    return nativeGetMid(this.nativeRtpTransceiver);
  }

  public RtpSender getSender() {
    return this.cachedSender;
  }

  public RtpReceiver getReceiver() {
    return this.cachedReceiver;
  }

  public boolean isStopped() {
    this.checkRtpTransceiverExists();
    return nativeStopped(this.nativeRtpTransceiver);
  }

  public RtpTransceiverDirection getDirection() {
    this.checkRtpTransceiverExists();
    return nativeDirection(this.nativeRtpTransceiver);
  }

  public RtpTransceiverDirection getCurrentDirection() {
    this.checkRtpTransceiverExists();
    return nativeCurrentDirection(this.nativeRtpTransceiver);
  }

  public boolean setDirection(RtpTransceiverDirection rtpTransceiverDirection) {
    this.checkRtpTransceiverExists();
    return nativeSetDirection(this.nativeRtpTransceiver, rtpTransceiverDirection);
  }

  public void stop() {
    this.checkRtpTransceiverExists();
    nativeStopInternal(this.nativeRtpTransceiver);
  }

  public void stopInternal() {
    this.checkRtpTransceiverExists();
    nativeStopInternal(this.nativeRtpTransceiver);
  }

  public void stopStandard() {
    this.checkRtpTransceiverExists();
    nativeStopStandard(this.nativeRtpTransceiver);
  }

  @CalledByNative
  public void dispose() {
    this.checkRtpTransceiverExists();
    this.cachedSender.dispose();
    this.cachedReceiver.dispose();
    JniCommon.nativeReleaseRef(this.nativeRtpTransceiver);
    this.nativeRtpTransceiver = 0L;
  }

  private void checkRtpTransceiverExists() {
    if (this.nativeRtpTransceiver == 0L) {
      throw new IllegalStateException("RtpTransceiver has been disposed.");
    }
  }

  private static native MediaStreamTrack.MediaType nativeGetMediaType(long var0);

  private static native String nativeGetMid(long var0);

  private static native RtpSender nativeGetSender(long var0);

  private static native RtpReceiver nativeGetReceiver(long var0);

  private static native boolean nativeStopped(long var0);

  private static native RtpTransceiverDirection nativeDirection(long var0);

  private static native RtpTransceiverDirection nativeCurrentDirection(long var0);

  private static native void nativeStopInternal(long var0);

  private static native void nativeStopStandard(long var0);

  private static native boolean nativeSetDirection(long var0, RtpTransceiverDirection var2);

  public static final class RtpTransceiverInit {
    private final RtpTransceiverDirection direction;
    private final List<String> streamIds;
    private final List<RtpParameters.Encoding> sendEncodings;

    public RtpTransceiverInit() {
      this(RtpTransceiver.RtpTransceiverDirection.SEND_RECV);
    }

    public RtpTransceiverInit(RtpTransceiverDirection direction) {
      this(direction, Collections.emptyList(), Collections.emptyList());
    }

    public RtpTransceiverInit(RtpTransceiverDirection direction, List<String> streamIds) {
      this(direction, streamIds, Collections.emptyList());
    }

    public RtpTransceiverInit(RtpTransceiverDirection direction, List<String> streamIds, List<RtpParameters.Encoding> sendEncodings) {
      this.direction = direction;
      this.streamIds = new ArrayList(streamIds);
      this.sendEncodings = new ArrayList(sendEncodings);
    }

    @CalledByNative("RtpTransceiverInit")
    int getDirectionNativeIndex() {
      return this.direction.getNativeIndex();
    }

    @CalledByNative("RtpTransceiverInit")
    List<String> getStreamIds() {
      return new ArrayList(this.streamIds);
    }

    @CalledByNative("RtpTransceiverInit")
    List<RtpParameters.Encoding> getSendEncodings() {
      return new ArrayList(this.sendEncodings);
    }
  }

  public static enum RtpTransceiverDirection {
    SEND_RECV(0),
    SEND_ONLY(1),
    RECV_ONLY(2),
    INACTIVE(3),
    STOPPED(4);

    private final int nativeIndex;

    private RtpTransceiverDirection(int nativeIndex) {
      this.nativeIndex = nativeIndex;
    }

    @CalledByNative("RtpTransceiverDirection")
    int getNativeIndex() {
      return this.nativeIndex;
    }

    @CalledByNative("RtpTransceiverDirection")
    static RtpTransceiverDirection fromNativeIndex(int nativeIndex) {
      RtpTransceiverDirection[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
        RtpTransceiverDirection type = var1[var3];
        if (type.getNativeIndex() == nativeIndex) {
          return type;
        }
      }

      throw new IllegalArgumentException("Uknown native RtpTransceiverDirection type" + nativeIndex);
    }
  }
}
