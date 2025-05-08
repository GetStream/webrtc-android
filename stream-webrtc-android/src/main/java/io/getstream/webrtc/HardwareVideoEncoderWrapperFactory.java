/*
 * Copyright (c) 2014-2024 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.getstream.webrtc;

/**
 * Original source: https://github.com/shiguredo/sora-android-sdk/blob/3cc88e806ab2f2327bf304207
 * 2e98d6da9df4408/sora-android-sdk/src/main/kotlin/jp/shiguredo/sora/sdk/codec/HardwareVideoEnco
 * derWrapperFactory.kt
 */
class HardwareVideoEncoderWrapperFactory implements VideoEncoderFactory {

    private static final String TAG = "HardwareVideoEncoderWrapperFactory";

    private final HardwareVideoEncoderFactory factory;
    private final int resolutionPixelAlignment;

    public HardwareVideoEncoderWrapperFactory(HardwareVideoEncoderFactory factory, int resolutionPixelAlignment) {
        this.factory = factory;
        this.resolutionPixelAlignment = resolutionPixelAlignment;
        if (resolutionPixelAlignment == 0) {
            throw new IllegalArgumentException("resolutionPixelAlignment should not be 0");
        }
    }

    @Override
    public VideoEncoder createEncoder(VideoCodecInfo videoCodecInfo) {
        try {
            VideoEncoder encoder = factory.createEncoder(videoCodecInfo);
            if (encoder == null) {
                return null;
            }
            return new HardwareVideoEncoderWrapper(encoder, resolutionPixelAlignment);
        } catch (Exception e) {
            Logging.e(TAG, "createEncoder failed", e);
            return null;
        }
    }

    @Override
    public VideoCodecInfo[] getSupportedCodecs() {
        return factory.getSupportedCodecs();
    }
}