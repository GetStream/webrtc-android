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
class HardwareVideoEncoderWrapper implements VideoEncoder {

    private static final String TAG = "HardwareVideoEncoderWrapper";

    private final VideoEncoder internalEncoder;
    private final int alignment;

    public HardwareVideoEncoderWrapper(VideoEncoder internalEncoder, int alignment) {
        this.internalEncoder = internalEncoder;
        this.alignment = alignment;
    }

    private static class CropSizeCalculator {

        private static final String TAG = "CropSizeCalculator";

        private final int alignment;
        private final int originalWidth;
        private final int originalHeight;
        private final int cropX;
        private final int cropY;

        public CropSizeCalculator(int alignment, int originalWidth, int originalHeight) {
            this.alignment = alignment;
            this.originalWidth = originalWidth;
            this.originalHeight = originalHeight;
            this.cropX = originalWidth % alignment;
            this.cropY = originalHeight % alignment;
            if (originalWidth != 0 && originalHeight != 0) {
                Logging.v(TAG, "init(): alignment=" + alignment +
                        " size=" + originalWidth + "x" + originalHeight + " => " + getCroppedWidth() + "x" + getCroppedHeight());
            }
        }

        public int getCroppedWidth() {
            return originalWidth - cropX;
        }

        public int getCroppedHeight() {
            return originalHeight - cropY;
        }

        public boolean isCropRequired() {
            return cropX != 0 || cropY != 0;
        }

        public boolean hasFrameSizeChanged(int nextWidth, int nextHeight) {
            if (originalWidth == nextWidth && originalHeight == nextHeight) {
                return false;
            } else {
                Logging.v(TAG, "frame size has changed: " +
                        originalWidth + "x" + originalHeight + " => " + nextWidth + "x" + nextHeight);
                return true;
            }
        }
    }

    private CropSizeCalculator calculator = new CropSizeCalculator(1, 0, 0);

    private VideoCodecStatus retryWithoutCropping(int width, int height, Runnable retryFunc) {
        Logging.v(TAG, "retrying without resolution adjustment");
        calculator = new CropSizeCalculator(1, width, height);
        retryFunc.run();
        return VideoCodecStatus.OK;
    }

    @Override
    public VideoCodecStatus initEncode(VideoEncoder.Settings originalSettings, VideoEncoder.Callback callback) {
        calculator = new CropSizeCalculator(alignment, originalSettings.width, originalSettings.height);
        if (!calculator.isCropRequired()) {
            return internalEncoder.initEncode(originalSettings, callback);
        } else {
            VideoEncoder.Settings croppedSettings = new VideoEncoder.Settings(
                    originalSettings.numberOfCores,
                    calculator.getCroppedWidth(),
                    calculator.getCroppedHeight(),
                    originalSettings.startBitrate,
                    originalSettings.maxFramerate,
                    originalSettings.numberOfSimulcastStreams,
                    originalSettings.automaticResizeOn,
                    originalSettings.capabilities
            );
            try {
                VideoCodecStatus result = internalEncoder.initEncode(croppedSettings, callback);
                if (result == VideoCodecStatus.FALLBACK_SOFTWARE) {
                    Logging.e(TAG, "internalEncoder.initEncode() returned FALLBACK_SOFTWARE: " +
                            "croppedSettings " + croppedSettings);
                    return retryWithoutCropping(
                            originalSettings.width,
                            originalSettings.height,
                            () -> internalEncoder.initEncode(originalSettings, callback)
                    );
                } else {
                    return result;
                }
            } catch (Exception e) {
                Logging.e(TAG, "internalEncoder.initEncode() failed", e);
                return retryWithoutCropping(
                        originalSettings.width,
                        originalSettings.height,
                        () -> internalEncoder.initEncode(originalSettings, callback)
                );
            }
        }
    }

    @Override
    public VideoCodecStatus release() {
        return internalEncoder.release();
    }

    @Override
    public VideoCodecStatus encode(VideoFrame frame, VideoEncoder.EncodeInfo encodeInfo) {
        if (calculator.hasFrameSizeChanged(frame.getBuffer().getWidth(), frame.getBuffer().getHeight())) {
            calculator = new CropSizeCalculator(alignment, frame.getBuffer().getWidth(), frame.getBuffer().getHeight());
        }
        if (!calculator.isCropRequired()) {
            return internalEncoder.encode(frame, encodeInfo);
        } else {
            int croppedWidth = calculator.getCroppedWidth();
            int croppedHeight = calculator.getCroppedHeight();
            VideoFrame.Buffer croppedBuffer = frame.getBuffer().cropAndScale(
                    calculator.cropX / 2,
                    calculator.cropY / 2,
                    croppedWidth,
                    croppedHeight,
                    croppedWidth,
                    croppedHeight
            );
            VideoFrame croppedFrame = new VideoFrame(croppedBuffer, frame.getRotation(), frame.getTimestampNs());
            try {
                VideoCodecStatus result = internalEncoder.encode(croppedFrame, encodeInfo);
                if (result == VideoCodecStatus.FALLBACK_SOFTWARE) {
                    Logging.e(TAG, "internalEncoder.encode() returned FALLBACK_SOFTWARE");
                    return retryWithoutCropping(
                            frame.getBuffer().getWidth(),
                            frame.getBuffer().getHeight(),
                            () -> internalEncoder.encode(frame, encodeInfo)
                    );
                } else {
                    return result;
                }
            } catch (Exception e) {
                Logging.e(TAG, "internalEncoder.encode() failed", e);
                return retryWithoutCropping(
                        frame.getBuffer().getWidth(),
                        frame.getBuffer().getHeight(),
                        () -> internalEncoder.encode(frame, encodeInfo)
                );
            } finally {
                croppedBuffer.release();
            }
        }
    }

    @Override
    public VideoCodecStatus setRateAllocation(VideoEncoder.BitrateAllocation allocation, int frameRate) {
        return internalEncoder.setRateAllocation(allocation, frameRate);
    }

    @Override
    public VideoEncoder.ScalingSettings getScalingSettings() {
        return internalEncoder.getScalingSettings();
    }

    @Override
    public String getImplementationName() {
        return internalEncoder.getImplementationName();
    }

    @Override
    public long createNative(long webrtcEnvRef) {
        return internalEncoder.createNative(webrtcEnvRef);
    }

    @Override
    public boolean isHardwareEncoder() {
        return internalEncoder.isHardwareEncoder();
    }

    @Override
    public VideoCodecStatus setRates(VideoEncoder.RateControlParameters rcParameters) {
        return internalEncoder.setRates(rcParameters);
    }

    @Override
    public VideoEncoder.ResolutionBitrateLimits[] getResolutionBitrateLimits() {
        return internalEncoder.getResolutionBitrateLimits();
    }

    @Override
    public VideoEncoder.EncoderInfo getEncoderInfo() {
        return internalEncoder.getEncoderInfo();
    }
}


