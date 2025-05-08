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
package io.getstream.webrtc;

/**
 * Resolution alignment values. Generally the MULTIPLE_OF_16 is recommended
 * for both VP8 and H264
 */
public enum ResolutionAdjustment {
    NONE(1),
    MULTIPLE_OF_2(2),
    MULTIPLE_OF_4(4),
    MULTIPLE_OF_8(8),
    MULTIPLE_OF_16(16);

    private final int value;

    private ResolutionAdjustment(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

