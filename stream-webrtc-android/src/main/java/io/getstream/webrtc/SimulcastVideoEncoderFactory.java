/*
 *  Copyright 2017 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package io.getstream.webrtc;

import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
 
public class SimulcastVideoEncoderFactory implements VideoEncoderFactory {
 
    static native List<VideoCodecInfo> nativeVP9Codecs();
    static native VideoCodecInfo nativeAV1Codec();

    VideoEncoderFactory primary;
    VideoEncoderFactory fallback;
 
    public SimulcastVideoEncoderFactory(VideoEncoderFactory primary, VideoEncoderFactory fallback) {
        this.primary = primary;
        this.fallback = fallback;
    }
 
    @Nullable
    @Override
    public VideoEncoder createEncoder(VideoCodecInfo info) {
        return new SimulcastVideoEncoder(primary, fallback, info);
    }
 
    @Override
    public VideoCodecInfo[] getSupportedCodecs() {
        List<VideoCodecInfo> codecs = new ArrayList<VideoCodecInfo>();
        codecs.addAll(Arrays.asList(primary.getSupportedCodecs()));
        if (fallback != null) {
            codecs.addAll(Arrays.asList(fallback.getSupportedCodecs()));
        }
        codecs.addAll(nativeVP9Codecs());
        codecs.add(nativeAV1Codec());
        return codecs.toArray(new VideoCodecInfo[codecs.size()]);
    }

}
