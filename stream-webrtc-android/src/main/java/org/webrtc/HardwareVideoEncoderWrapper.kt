/*
 * Copyright (c) 2014-2023 Stream.io Inc. All rights reserved.
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

package org.webrtc

/**
 * Original source: https://github.com/shiguredo/sora-android-sdk/blob/3cc88e806ab2f2327bf304207
 * 2e98d6da9df4408/sora-android-sdk/src/main/kotlin/jp/shiguredo/sora/sdk/codec/HardwareVideoEnco
 * derWrapperFactory.kt
 */
internal class HardwareVideoEncoderWrapper(
  private val internalEncoder: VideoEncoder,
  private val alignment: Int,
) : VideoEncoder {
  class CropSizeCalculator(
    alignment: Int,
    private val originalWidth: Int,
    private val originalHeight: Int,
  ) {

    companion object {
      val TAG = CropSizeCalculator::class.simpleName
    }

    val cropX: Int = originalWidth % alignment
    val cropY: Int = originalHeight % alignment

    val croppedWidth: Int
      get() = originalWidth - cropX

    val croppedHeight: Int
      get() = originalHeight - cropY

    val isCropRequired: Boolean
      get() = cropX != 0 || cropY != 0

    init {
      if (originalWidth != 0 && originalHeight != 0) {
        Logging.v(
          TAG,
          "$this init(): alignment=$alignment" +
            "" +
            " size=${originalWidth}x$originalHeight => ${croppedWidth}x$croppedHeight",
        )
      }
    }

    fun hasFrameSizeChanged(nextWidth: Int, nextHeight: Int): Boolean {
      return if (originalWidth == nextWidth && originalHeight == nextHeight) {
        false
      } else {
        Logging.v(
          TAG,
          "frame size has changed: " +
            "${originalWidth}x$originalHeight => ${nextWidth}x$nextHeight",
        )
        true
      }
    }
  }

  companion object {
    val TAG = HardwareVideoEncoderWrapper::class.simpleName
  }

  private var calculator = CropSizeCalculator(1, 0, 0)

  private fun retryWithoutCropping(
    width: Int,
    height: Int,
    retryFunc: () -> VideoCodecStatus,
  ): VideoCodecStatus {
    Logging.v(TAG, "retrying without resolution adjustment")

    calculator = CropSizeCalculator(1, width, height)

    return retryFunc()
  }

  override fun initEncode(
    originalSettings: VideoEncoder.Settings,
    callback: VideoEncoder.Callback?,
  ): VideoCodecStatus {
    calculator = CropSizeCalculator(alignment, originalSettings.width, originalSettings.height)

    if (!calculator.isCropRequired) {
      return internalEncoder.initEncode(originalSettings, callback)
    } else {
      val croppedSettings = VideoEncoder.Settings(
        originalSettings.numberOfCores,
        calculator.croppedWidth,
        calculator.croppedHeight,
        originalSettings.startBitrate,
        originalSettings.maxFramerate,
        originalSettings.numberOfSimulcastStreams,
        originalSettings.automaticResizeOn,
        originalSettings.capabilities,
      )

      try {
        val result = internalEncoder.initEncode(croppedSettings, callback)
        return if (result == VideoCodecStatus.FALLBACK_SOFTWARE) {
          Logging.e(
            TAG,
            "internalEncoder.initEncode() returned FALLBACK_SOFTWARE: " +
              "croppedSettings $croppedSettings",
          )
          retryWithoutCropping(
            originalSettings.width,
            originalSettings.height,
          ) { internalEncoder.initEncode(originalSettings, callback) }
        } else {
          result
        }
      } catch (e: Exception) {
        Logging.e(TAG, "internalEncoder.initEncode() failed", e)
        return retryWithoutCropping(
          originalSettings.width,
          originalSettings.height,
        ) { internalEncoder.initEncode(originalSettings, callback) }
      }
    }
  }

  override fun release(): VideoCodecStatus {
    return internalEncoder.release()
  }

  override fun encode(frame: VideoFrame, encodeInfo: VideoEncoder.EncodeInfo?): VideoCodecStatus {
    if (calculator.hasFrameSizeChanged(frame.buffer.width, frame.buffer.height)) {
      calculator = CropSizeCalculator(alignment, frame.buffer.width, frame.buffer.height)
    }

    if (!calculator.isCropRequired) {
      return internalEncoder.encode(frame, encodeInfo)
    } else {
      // https://source.chromium.org/chromium/chromium/src/+/main:third_party/webrtc/sdk/android/api/org/webrtc/JavaI420Buffer.java;l=172-185;drc=02334e07c5c04c729dd3a8a279bb1fbe24ee8b7c
      val croppedWidth = calculator.croppedWidth
      val croppedHeight = calculator.croppedHeight
      val croppedBuffer = frame.buffer.cropAndScale(
        calculator.cropX / 2,
        calculator.cropY / 2,
        croppedWidth,
        croppedHeight,
        croppedWidth,
        croppedHeight,
      )

      val croppedFrame = VideoFrame(croppedBuffer, frame.rotation, frame.timestampNs)

      try {
        val result = internalEncoder.encode(croppedFrame, encodeInfo)
        return if (result == VideoCodecStatus.FALLBACK_SOFTWARE) {
          Logging.e(TAG, "internalEncoder.encode() returned FALLBACK_SOFTWARE")
          retryWithoutCropping(frame.buffer.width, frame.buffer.height) {
            internalEncoder.encode(
              frame,
              encodeInfo,
            )
          }
        } else {
          result
        }
      } catch (e: Exception) {
        Logging.e(TAG, "internalEncoder.encode() failed", e)
        return retryWithoutCropping(
          frame.buffer.width,
          frame.buffer.height,
        ) { internalEncoder.encode(frame, encodeInfo) }
      } finally {
        croppedBuffer.release()
      }
    }
  }

  override fun setRateAllocation(
    allocation: VideoEncoder.BitrateAllocation?,
    frameRate: Int,
  ): VideoCodecStatus {
    return internalEncoder.setRateAllocation(allocation, frameRate)
  }

  override fun getScalingSettings(): VideoEncoder.ScalingSettings {
    return internalEncoder.scalingSettings
  }

  override fun getImplementationName(): String {
    return internalEncoder.implementationName
  }

  override fun createNativeVideoEncoder(): Long {
    return internalEncoder.createNativeVideoEncoder()
  }

  override fun isHardwareEncoder(): Boolean {
    return internalEncoder.isHardwareEncoder
  }

  override fun setRates(rcParameters: VideoEncoder.RateControlParameters?): VideoCodecStatus {
    return internalEncoder.setRates(rcParameters)
  }

  override fun getResolutionBitrateLimits(): Array<VideoEncoder.ResolutionBitrateLimits> {
    return internalEncoder.resolutionBitrateLimits
  }

  override fun getEncoderInfo(): VideoEncoder.EncoderInfo {
    return internalEncoder.encoderInfo
  }
}

internal class HardwareVideoEncoderWrapperFactory(
  private val factory: HardwareVideoEncoderFactory,
  private val resolutionPixelAlignment: Int,
) : VideoEncoderFactory {
  companion object {
    val TAG = HardwareVideoEncoderWrapperFactory::class.simpleName
  }

  init {
    if (resolutionPixelAlignment == 0) {
      throw java.lang.Exception("resolutionPixelAlignment should not be 0")
    }
  }

  override fun createEncoder(videoCodecInfo: VideoCodecInfo?): VideoEncoder? {
    try {
      val encoder = factory.createEncoder(videoCodecInfo) ?: return null
      return HardwareVideoEncoderWrapper(encoder, resolutionPixelAlignment)
    } catch (e: Exception) {
      Logging.e(TAG, "createEncoder failed", e)
      return null
    }
  }

  override fun getSupportedCodecs(): Array<VideoCodecInfo> {
    return factory.supportedCodecs
  }
}
