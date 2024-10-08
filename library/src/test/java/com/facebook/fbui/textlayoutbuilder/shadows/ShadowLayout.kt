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

import android.text.Layout
import android.text.TextPaint
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

@Implements(Layout::class)
object ShadowLayout {

  const val LONG_TEXT: String = "ZzZzZzZzZzZzZzZzZzZzZz"
  const val SHORT_TEXT: String = "Z"
  const val LONG_TEXT_LENGTH: Int = 100
  const val SHORT_TEXT_LENGTH: Int = 5

  @JvmStatic
  @Implementation
  fun getDesiredWidth(source: CharSequence, paint: TextPaint?): Float {
    if (LONG_TEXT == source) {
      return LONG_TEXT_LENGTH.toFloat()
    } else if (SHORT_TEXT == source) {
      return SHORT_TEXT_LENGTH.toFloat()
    }
    return 0f
  }
}
