/*
 * Copyright 2023 LiveKit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.webrtc;

import androidx.annotation.Nullable;
import java.util.List;
import java.util.Map;
import org.webrtc.MediaStreamTrack;

public class RtpCapabilities {
  public static class CodecCapability {
    public int preferredPayloadType;
    // Name used to identify the codec. Equivalent to MIME subtype.
    public String name;
    // The media type of this codec. Equivalent to MIME top-level type.
    public MediaStreamTrack.MediaType kind;
    // Clock rate in Hertz.
    public Integer clockRate;
    // The number of audio channels used. Set to null for video codecs.
    public Integer numChannels;
    // The "format specific parameters" field from the "a=fmtp" line in the SDP
    public Map<String, String> parameters;
    // The MIME type of the codec. This is a convenience field.
    public String mimeType;

    public CodecCapability() {}

    @CalledByNative("CodecCapability")
    CodecCapability(int preferredPayloadType, String name, MediaStreamTrack.MediaType kind,
        Integer clockRate, Integer numChannels, String mimeType, Map<String, String> parameters) {
      this.preferredPayloadType = preferredPayloadType;
      this.name = name;
      this.kind = kind;
      this.clockRate = clockRate;
      this.numChannels = numChannels;
      this.parameters = parameters;
      this.mimeType = mimeType;
    }

    @CalledByNative("CodecCapability")
    int getPreferredPayloadType() {
      return preferredPayloadType;
    }

    @CalledByNative("CodecCapability")
    String getName() {
      return name;
    }

    @CalledByNative("CodecCapability")
    MediaStreamTrack.MediaType getKind() {
      return kind;
    }

    @CalledByNative("CodecCapability")
    Integer getClockRate() {
      return clockRate;
    }

    @CalledByNative("CodecCapability")
    Integer getNumChannels() {
      return numChannels;
    }

    @CalledByNative("CodecCapability")
    Map getParameters() {
      return parameters;
    }
  }

  public static class HeaderExtensionCapability {
    private final String uri;
    private final int preferredId;
    private final boolean preferredEncrypted;

    @CalledByNative("HeaderExtensionCapability")
    HeaderExtensionCapability(String uri, int preferredId, boolean preferredEncrypted) {
      this.uri = uri;
      this.preferredId = preferredId;
      this.preferredEncrypted = preferredEncrypted;
    }

    @CalledByNative("HeaderExtensionCapability")
    public String getUri() {
      return uri;
    }

    @CalledByNative("HeaderExtensionCapability")
    public int getPreferredId() {
      return preferredId;
    }

    @CalledByNative("HeaderExtensionCapability")
    public boolean getPreferredEncrypted() {
      return preferredEncrypted;
    }
  }

  public List<CodecCapability> codecs;
  public List<HeaderExtensionCapability> headerExtensions;

  @CalledByNative
  RtpCapabilities(List<CodecCapability> codecs, List<HeaderExtensionCapability> headerExtensions) {
    this.headerExtensions = headerExtensions;
    this.codecs = codecs;
  }

  @CalledByNative
  public List<HeaderExtensionCapability> getHeaderExtensions() {
    return headerExtensions;
  }

  @CalledByNative
  List<CodecCapability> getCodecs() {
    return codecs;
  }
}