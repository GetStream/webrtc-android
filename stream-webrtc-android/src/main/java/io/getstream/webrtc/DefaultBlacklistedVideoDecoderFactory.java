package io.getstream.webrtc;

import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class DefaultBlacklistedVideoDecoderFactory implements VideoDecoderFactory {

  private static final String TAG = "DefaultBlacklistedVideoDecoderFactory";

  private static final Predicate<VideoDecoder> defaultBlacklistedPredicate =
    new Predicate<VideoDecoder>() {
      @Override
      public boolean test(@Nullable VideoDecoder decoder) {
        // if the decoder is Exynos VP9, then blacklist it
        return isExynosVP9(decoder);
      }
    };

  private final VideoDecoderFactory hardwareVideoDecoderFactory;
  private final VideoDecoderFactory softwareVideoDecoderFactory;
  private final VideoDecoderFactory platformSoftwareVideoDecoderFactory;
  private final Predicate<VideoDecoder> isHardwareDecoderBlacklisted;

  public DefaultBlacklistedVideoDecoderFactory(@Nullable EglBase.Context eglContext) {
    this(eglContext, null);
  }

  public DefaultBlacklistedVideoDecoderFactory(
      @Nullable EglBase.Context eglContext, 
      @Nullable Predicate<VideoDecoder> decoderBlacklistedPredicate) {
    this.hardwareVideoDecoderFactory = new HardwareVideoDecoderFactory(eglContext);
    this.softwareVideoDecoderFactory = new SoftwareVideoDecoderFactory();
    this.platformSoftwareVideoDecoderFactory = new PlatformSoftwareVideoDecoderFactory(eglContext);
    this.isHardwareDecoderBlacklisted = decoderBlacklistedPredicate == null
      ? defaultBlacklistedPredicate
      : decoderBlacklistedPredicate.or(defaultBlacklistedPredicate);
  }

  @Override
  public VideoDecoder createDecoder(VideoCodecInfo codecType) {
    VideoDecoder softwareDecoder = softwareVideoDecoderFactory.createDecoder(codecType);
    VideoDecoder hardwareDecoder = hardwareVideoDecoderFactory.createDecoder(codecType);
    if (softwareDecoder == null) {
      softwareDecoder = platformSoftwareVideoDecoderFactory.createDecoder(codecType);
    }

    if (isHardwareDecoderBlacklisted.test(hardwareDecoder)) {
      Logging.d(TAG, "Hardware decoder is blacklisted: " + hardwareDecoder.getImplementationName());
      return softwareDecoder;
    }

    if (hardwareDecoder != null && softwareDecoder != null) {
      return new VideoDecoderFallback(softwareDecoder, hardwareDecoder);
    } else {
      return hardwareDecoder != null ? hardwareDecoder : softwareDecoder;
    }
  }

  @Override
  public VideoCodecInfo[] getSupportedCodecs() {
    Set<VideoCodecInfo> supportedCodecInfos = new HashSet<>();
    supportedCodecInfos.addAll(Arrays.asList(softwareVideoDecoderFactory.getSupportedCodecs()));
    supportedCodecInfos.addAll(Arrays.asList(hardwareVideoDecoderFactory.getSupportedCodecs()));
    supportedCodecInfos.addAll(Arrays.asList(platformSoftwareVideoDecoderFactory.getSupportedCodecs()));
    return supportedCodecInfos.toArray(new VideoCodecInfo[0]);
  }

  private static boolean isExynosVP9(@Nullable VideoDecoder decoder) {
    if (decoder == null) {
      return false;
    }
    final String name = decoder.getImplementationName().toLowerCase();
    return name.contains("exynos") && name.contains("vp9");
  }
}
