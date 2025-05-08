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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The main difference with the standard SimulcastVideoEncoderFactory is that this fixes issues
 * with simulcasting resolutions that are not aligned (e.g. VP8 requires 16x16 alignment). You can
 * set the alignment by setting resolutionAdjustment. Internally the resolutions during simulcast
 * will be cropped to comply with the adjustment. Fallback behaviour is the same as with the
 * standard SimulcastVideoEncoderFactory and it will use the SW encoder if HW fails
 * or is not available.
 *
 * Original source: https://github.com/shiguredo/sora-android-sdk/blob/3cc88e806ab2f2327bf3042072
 * e98d6da9df4408/sora-android-sdk/src/main/kotlin/jp/shiguredo/sora/sdk/codec/SimulcastVideoEnc
 * oderFactoryWrapper.kt#L18
 */
public class SimulcastAlignedVideoEncoderFactory implements VideoEncoderFactory {
    private static class StreamEncoderWrapper implements VideoEncoder {
        private static final String TAG = "StreamEncoderWrapper";
        private final VideoEncoder encoder;
        private final ExecutorService executor = Executors.newSingleThreadExecutor();
        private VideoEncoder.Settings streamSettings;

        public StreamEncoderWrapper(VideoEncoder encoder) {
            this.encoder = encoder;
        }

        @Override
        public VideoCodecStatus initEncode(VideoEncoder.Settings settings, VideoEncoder.Callback callback) {
            streamSettings = settings;
            Callable<VideoCodecStatus> callable = () -> {
                Logging.v(TAG, "initEncode() thread=" + Thread.currentThread().getName() + " [" + Thread.currentThread().getId() + "]");
                Logging.v(TAG, "  encoder=" + encoder.getImplementationName());
                Logging.v(TAG, "  streamSettings:");
                Logging.v(TAG, "    numberOfCores=" + settings.numberOfCores);
                Logging.v(TAG, "    width=" + settings.width);
                Logging.v(TAG, "    height=" + settings.height);
                Logging.v(TAG, "    startBitrate=" + settings.startBitrate);
                Logging.v(TAG, "    maxFramerate=" + settings.maxFramerate);
                Logging.v(TAG, "    automaticResizeOn=" + settings.automaticResizeOn);
                Logging.v(TAG, "    numberOfSimulcastStreams=" + settings.numberOfSimulcastStreams);
                Logging.v(TAG, "    lossNotification=" + settings.capabilities.lossNotification);
                return encoder.initEncode(settings, callback);
            };
            try {
                return executor.submit(callable).get();
            } catch (Exception e) {
                return VideoCodecStatus.ERROR;
            }
        }

        @Override
        public VideoCodecStatus release() {
            Callable<VideoCodecStatus> callable = () -> encoder.release();
            try {
                return executor.submit(callable).get();
            } catch (Exception e) {
                return VideoCodecStatus.ERROR;
            }
        }

        @Override
        public VideoCodecStatus encode(VideoFrame frame, VideoEncoder.EncodeInfo encodeInfo) {
            Callable<VideoCodecStatus> callable = () -> {
                if (streamSettings != null) {
                    if (frame.getBuffer().getWidth() == streamSettings.width) {
                        return encoder.encode(frame, encodeInfo);
                    } else {
                        int originalWidth = frame.getBuffer().getWidth();
                        int originalHeight = frame.getBuffer().getHeight();
                        VideoFrame.Buffer scaledBuffer = frame.getBuffer().cropAndScale(
                                0, 0, originalWidth, originalHeight,
                                streamSettings.width, streamSettings.height
                        );
                        VideoFrame scaledFrame = new VideoFrame(scaledBuffer, frame.getRotation(), frame.getTimestampNs());
                        VideoCodecStatus result = encoder.encode(scaledFrame, encodeInfo);
                        scaledBuffer.release();
                        return result;
                    }
                } else {
                    return VideoCodecStatus.ERROR;
                }
            };
            try {
                return executor.submit(callable).get();
            } catch (Exception e) {
                return VideoCodecStatus.ERROR;
            }
        }

        @Override
        public VideoCodecStatus setRateAllocation(VideoEncoder.BitrateAllocation allocation, int frameRate) {
            Callable<VideoCodecStatus> callable = () -> encoder.setRateAllocation(allocation, frameRate);
            try {
                return executor.submit(callable).get();
            } catch (Exception e) {
                return VideoCodecStatus.ERROR;
            }
        }

        @Override
        public VideoEncoder.ScalingSettings getScalingSettings() {
            Callable<VideoEncoder.ScalingSettings> callable = () -> encoder.getScalingSettings();
            try {
                return executor.submit(callable).get();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public String getImplementationName() {
            Callable<String> callable = () -> encoder.getImplementationName();
            try {
                return executor.submit(callable).get();
            } catch (Exception e) {
                return null;
            }
        }
    }

    private static class StreamEncoderWrapperFactory implements VideoEncoderFactory {
        private final VideoEncoderFactory factory;

        public StreamEncoderWrapperFactory(VideoEncoderFactory factory) {
            this.factory = factory;
        }

        @Override
        public VideoEncoder createEncoder(VideoCodecInfo videoCodecInfo) {
            VideoEncoder encoder = factory.createEncoder(videoCodecInfo);
            if (encoder == null) {
                return null;
            }
            return new StreamEncoderWrapper(encoder);
        }

        @Override
        public VideoCodecInfo[] getSupportedCodecs() {
            return factory.getSupportedCodecs();
        }
    }

    private final VideoEncoderFactory primary;
    private final VideoEncoderFactory fallback;
    private final SimulcastVideoEncoderFactory delegate;

    public SimulcastAlignedVideoEncoderFactory(EglBase.Context sharedContext, boolean enableIntelVp8Encoder, boolean enableH264HighProfile, ResolutionAdjustment resolutionAdjustment) {
        HardwareVideoEncoderFactory hardwareVideoEncoderFactory = new HardwareVideoEncoderFactory(sharedContext, enableIntelVp8Encoder, enableH264HighProfile);
        VideoEncoderFactory encoderFactory;
        if (resolutionAdjustment == ResolutionAdjustment.NONE) {
            encoderFactory = hardwareVideoEncoderFactory;
        } else {
            encoderFactory = new HardwareVideoEncoderWrapperFactory(hardwareVideoEncoderFactory, resolutionAdjustment.getValue());
        }
        primary = new StreamEncoderWrapperFactory(encoderFactory);
        fallback = new SoftwareVideoEncoderFactory();
        delegate = new SimulcastVideoEncoderFactory(primary, fallback);
    }

    @Override
    public VideoEncoder createEncoder(VideoCodecInfo info) {
        return delegate.createEncoder(info);
    }

    @Override
    public VideoCodecInfo[] getSupportedCodecs() {
        return delegate.getSupportedCodecs();
    }
}


