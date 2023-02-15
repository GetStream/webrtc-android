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

package io.getstream.webrtc.android.ktx

import org.webrtc.AddIceObserver
import org.webrtc.IceCandidate
import org.webrtc.PeerConnection
import org.webrtc.WebRTCException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Add a given [IceCandidate] to a [PeerConnection].
 *
 * @param iceCandidate A given [IceCandidate].
 */
public suspend fun PeerConnection.addRtcIceCandidate(iceCandidate: IceCandidate): Result<Unit> {
  return suspendCoroutine { cont ->
    addIceCandidate(
      iceCandidate,
      object : AddIceObserver {
        override fun onAddSuccess() {
          cont.resume(Result.success(Unit))
        }

        override fun onAddFailure(error: String?) {
          cont.resume(Result.failure(WebRTCException(message = error)))
        }
      }
    )
  }
}
