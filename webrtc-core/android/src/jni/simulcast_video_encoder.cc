#include <jni.h>

#include "sdk/android/src/jni/jni_helpers.h"
#include "sdk/android/src/jni/video_encoder_factory_wrapper.h"
#include "sdk/android/src/jni/video_codec_info.h"
#include "sdk/android/native_api/codecs/wrapper.h"
#include "media/engine/simulcast_encoder_adapter.h"
#include "rtc_base/logging.h"

using namespace webrtc;
using namespace webrtc::jni;

#ifdef __cplusplus
extern "C" {
#endif

// (VideoEncoderFactory primary, VideoEncoderFactory fallback, VideoCodecInfo info)
JNIEXPORT jlong JNICALL Java_org_webrtc_SimulcastVideoEncoder_nativeCreateEncoder(JNIEnv *env, jclass klass, jobject primary, jobject fallback, jobject info) {
    RTC_LOG(LS_INFO) << "Create simulcast video encoder";
    JavaParamRef<jobject> info_ref(info);
    SdpVideoFormat format = VideoCodecInfoToSdpVideoFormat(env, info_ref);

    // TODO: 影響は軽微だが、リークする可能性があるので将来的に修正したい
    // https://github.com/shiguredo-webrtc-build/webrtc-build/pull/16#pullrequestreview-600675795
    return NativeToJavaPointer(std::make_unique<SimulcastEncoderAdapter>(
			    JavaToNativeVideoEncoderFactory(env, primary).release(),
			    JavaToNativeVideoEncoderFactory(env, fallback).release(),
			    format).release());
}


#ifdef __cplusplus
}
#endif
