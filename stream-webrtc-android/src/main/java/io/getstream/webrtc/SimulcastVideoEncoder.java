package io.getstream.webrtc;

public class SimulcastVideoEncoder extends WrappedNativeVideoEncoder {

    static native long nativeCreateEncoder(long webrtcEnvRef, VideoEncoderFactory primary, VideoEncoderFactory fallback, VideoCodecInfo info);

    VideoEncoderFactory primary;
    VideoEncoderFactory fallback;
    VideoCodecInfo info;

    public SimulcastVideoEncoder(VideoEncoderFactory primary, VideoEncoderFactory fallback, VideoCodecInfo info) {
        this.primary = primary;
        this.fallback = fallback;
        this.info = info;
    }

    @Override
    public long createNative(long webrtcEnvRef) {
        return nativeCreateEncoder(webrtcEnvRef, primary, fallback, info);
    }

    @Override
    public boolean isHardwareEncoder() {
        return false;
    }

}

