/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
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

package com.facebook.fbui.textlayoutbuilder.shadows

import android.graphics.Canvas
import android.graphics.Picture
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

@Implements(Picture::class)
class ShadowPicture {

  @Implementation
  fun __constructor__(nativePicture: Int, fromStream: Boolean) {
    // Do nothing.
  }

  @Implementation
  fun __constructor__(nativePicture: Int) {
    // Do nothing.
  }

  @Implementation
  fun __constructor__(nativePicture: Long) {
    // Do nothing.
  }

  @Implementation
  fun __constructor__() {
    // Do nothing.
  }

  @Implementation fun beginRecording(width: Int, height: Int): Canvas = Canvas()
}
