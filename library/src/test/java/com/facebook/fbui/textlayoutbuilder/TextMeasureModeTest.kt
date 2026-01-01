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

package com.facebook.fbui.textlayoutbuilder

import android.graphics.Typeface
import com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder.Companion.MEASURE_MODE_AT_MOST
import com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder.Companion.MEASURE_MODE_EXACTLY
import com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder.Companion.MEASURE_MODE_UNSPECIFIED
import com.facebook.fbui.textlayoutbuilder.shadows.ShadowLayout
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE, shadows = [ShadowLayout::class])
@RunWith(RobolectricTestRunner::class)
class TextMeasureModeTest {

  @Test
  fun testMeasureModeUnspecified() {
    val layout =
        TextLayoutBuilder()
            .setText(ShadowLayout.LONG_TEXT)
            .setWidth(20, MEASURE_MODE_UNSPECIFIED)
            .setTypeface(Typeface.DEFAULT)
            .setTextSize(10)
            .build()

    assertThat(layout?.width).isEqualTo(ShadowLayout.LONG_TEXT_LENGTH)
  }

  @Test
  fun testMeasureModeExactly() {
    val layout =
        TextLayoutBuilder()
            .setText(ShadowLayout.LONG_TEXT)
            .setWidth(20, MEASURE_MODE_EXACTLY)
            .setTypeface(Typeface.DEFAULT)
            .setTextSize(10)
            .build()

    assertThat(layout?.width).isEqualTo(20)
  }

  @Test
  fun testMeasureModeAtMostLongText() {
    val layout =
        TextLayoutBuilder()
            .setText(ShadowLayout.LONG_TEXT)
            .setWidth(20, MEASURE_MODE_AT_MOST)
            .setTypeface(Typeface.DEFAULT)
            .setTextSize(10)
            .build()

    assertThat(layout?.width).isEqualTo(20)
  }

  @Test
  fun testMeasureModeAtMostShortText() {
    val layout =
        TextLayoutBuilder()
            .setText(ShadowLayout.SHORT_TEXT)
            .setWidth(20, MEASURE_MODE_AT_MOST)
            .setTypeface(Typeface.DEFAULT)
            .setTextSize(10)
            .build()

    assertThat(layout?.width).isEqualTo(ShadowLayout.SHORT_TEXT_LENGTH)
  }

  @Test
  fun testLegacyBehaviour() {
    val layout =
        TextLayoutBuilder()
            .setText(ShadowLayout.LONG_TEXT)
            .setWidth(-1)
            .setTypeface(Typeface.DEFAULT)
            .setTextSize(10)
            .build()

    assertThat(layout?.width).isEqualTo(ShadowLayout.LONG_TEXT_LENGTH)
  }
}
