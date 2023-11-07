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

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * The main difference with the standard [SimulcastVideoEncoderFactory] is that this fixes issues
 * with simulcasting resolutions that are not aligned (e.g. VP8 requires 16x16 alignment). You can
 * set the alignment by setting [resolutionAdjustment]. Internally the resolutions during simulcast
 * will be cropped to comply with the adjustment. Fallback behaviour is the same as with the
 * standard [SimulcastVideoEncoderFactory] and it will use the SW encoder if HW fails
 * or is not available.
 *
 * Original source: https://github.com/shiguredo/sora-android-sdk/blob/3cc88e806ab2f2327bf3042072
 * e98d6da9df4408/sora-android-sdk/src/main/kotlin/jp/shiguredo/sora/sdk/codec/SimulcastVideoEnc
 * oderFactoryWrapper.kt#L18
 */
class SimulcastAlignedVideoEncoderFactory(
  sharedContext: EglBase.Context?,
  enableIntelVp8Encoder: Boolean = true,
  enableH264HighProfile: Boolean = false,
  resolutionAdjustment: ResolutionAdjustment,
) : VideoEncoderFactory {

  private class StreamEncoderWrapper(
    private val encoder: VideoEncoder,
  ) : VideoEncoder {
    companion object {
      val TAG = StreamEncoderWrapper::class.simpleName
    }

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private var streamSettings: VideoEncoder.Settings? = null

    override fun initEncode(
      settings: VideoEncoder.Settings,
      callback: VideoEncoder.Callback?,
    ): VideoCodecStatus {
      streamSettings = settings
      val future = executor.submit(
        Callable {
          Logging.v(
            TAG,
            """initEncode() thread=${Thread.currentThread().name} [${Thread.currentThread().id}]
                        |  encoder=${encoder.implementationName}
                        |  streamSettings:
                        |    numberOfCores=${settings.numberOfCores}
                        |    width=${settings.width}
                        |    height=${settings.height}
                        |    startBitrate=${settings.startBitrate}
                        |    maxFramerate=${settings.maxFramerate}
                        |    automaticResizeOn=${settings.automaticResizeOn}
                        |    numberOfSimulcastStreams=${settings.numberOfSimulcastStreams}
                        |    lossNotification=${settings.capabilities.lossNotification}
            """.trimMargin(),
          )
          return@Callable encoder.initEncode(settings, callback)
        },
      )
      return future.get()
    }

    override fun release(): VideoCodecStatus {
      val future = executor.submit(Callable { return@Callable encoder.release() })
      return future.get()
    }

    override fun encode(frame: VideoFrame, encodeInfo: VideoEncoder.EncodeInfo?): VideoCodecStatus {
      val future = executor.submit(
        Callable {
          return@Callable streamSettings?.let {
            if (frame.buffer.width == it.width) {
              encoder.encode(frame, encodeInfo)
            } else {
              val originalWidth = frame.buffer.width
              val originalHeight = frame.buffer.height
              val scaledBuffer = frame.buffer.cropAndScale(
                0, 0, originalWidth, originalHeight,
                it.width, it.height,
              )
              val scaledFrame = VideoFrame(scaledBuffer, frame.rotation, frame.timestampNs)
              val result = encoder.encode(scaledFrame, encodeInfo)
              scaledBuffer.release()
              result
            }
          } ?: run {
            VideoCodecStatus.ERROR
          }
        },
      )
      return future.get()
    }

    override fun setRateAllocation(
      allocation: VideoEncoder.BitrateAllocation?,
      frameRate: Int,
    ): VideoCodecStatus {
      val future = executor.submit(
        Callable {
          return@Callable encoder.setRateAllocation(
            allocation,
            frameRate,
          )
        },
      )
      return future.get()
    }

    override fun getScalingSettings(): VideoEncoder.ScalingSettings {
      val future = executor.submit(Callable { return@Callable encoder.scalingSettings })
      return future.get()
    }

    override fun getImplementationName(): String {
      val future = executor.submit(Callable { return@Callable encoder.implementationName })
      return future.get()
    }
  }

  private class StreamEncoderWrapperFactory(
    private val factory: VideoEncoderFactory,
  ) : VideoEncoderFactory {
    override fun createEncoder(videoCodecInfo: VideoCodecInfo?): VideoEncoder? {
      val encoder = factory.createEncoder(videoCodecInfo)
      if (encoder == null) {
        return null
      }
      return StreamEncoderWrapper(encoder)
    }

    override fun getSupportedCodecs(): Array<VideoCodecInfo> {
      return factory.supportedCodecs
    }
  }

  private val primary: VideoEncoderFactory
  private val fallback: VideoEncoderFactory?
  private val native: SimulcastVideoEncoderFactory

  init {
    val hardwareVideoEncoderFactory = HardwareVideoEncoderFactory(
      sharedContext,
      enableIntelVp8Encoder,
      enableH264HighProfile,
    )

    val encoderFactory = if (resolutionAdjustment == ResolutionAdjustment.NONE) {
      hardwareVideoEncoderFactory
    } else {
      HardwareVideoEncoderWrapperFactory(
        hardwareVideoEncoderFactory,
        resolutionAdjustment.value,
      )
    }

    primary = StreamEncoderWrapperFactory(encoderFactory)
    fallback = SoftwareVideoEncoderFactory()
    native = SimulcastVideoEncoderFactory(primary, fallback)
  }

  override fun createEncoder(info: VideoCodecInfo?): VideoEncoder? {
    return native.createEncoder(info)
  }

  override fun getSupportedCodecs(): Array<VideoCodecInfo> {
    return native.supportedCodecs
  }
}
