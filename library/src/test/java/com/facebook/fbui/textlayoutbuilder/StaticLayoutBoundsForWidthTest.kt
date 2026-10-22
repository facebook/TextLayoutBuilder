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

import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.annotation.RequiresApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RequiresApi(35)
@Config(sdk = [35])
@RunWith(RobolectricTestRunner::class)
class StaticLayoutBoundsForWidthTest {

  @Before
  fun setup() {
    TextLayoutBuilder.sCache.evictAll()
  }

  @After
  fun tearDown() {
    TextLayoutBuilderConfiguration.resetStaticLayoutBoundsForWidth = false
  }

  @Test
  fun `gate off keeps whatever the pooled builder carried`() {
    TextLayoutBuilderConfiguration.resetStaticLayoutBoundsForWidth = false
    dirtyBuilderPool()

    val layout = buildLayout()

    assertThat(layout.useBoundsForWidth).isTrue()
    assertThat(layout.shiftDrawingOffsetForStartOverhang).isTrue()
  }

  @Test
  fun `gate on resets flags a pooled builder carried`() {
    TextLayoutBuilderConfiguration.resetStaticLayoutBoundsForWidth = true
    dirtyBuilderPool()

    val layout = buildLayout()

    assertThat(layout.useBoundsForWidth).isFalse()
    assertThat(layout.shiftDrawingOffsetForStartOverhang).isFalse()
  }

  @Test
  fun `gate on leaves a clean builder pool unchanged`() {
    TextLayoutBuilderConfiguration.resetStaticLayoutBoundsForWidth = true

    val layout = buildLayout()

    assertThat(layout.useBoundsForWidth).isFalse()
    assertThat(layout.shiftDrawingOffsetForStartOverhang).isFalse()
  }

  /**
   * Mimics a `TextView` that targets API 35+: it builds with both flags on, then pools the builder.
   */
  private fun dirtyBuilderPool() {
    repeat(POOL_SIZE) {
      StaticLayout.Builder.obtain(TEXT, 0, TEXT.length, TextPaint(), WIDTH)
          .setUseBoundsForWidth(true)
          .setShiftDrawingOffsetForStartOverhang(true)
          .build()
    }
  }

  private fun buildLayout(): Layout = checkNotNull(
      TextLayoutBuilder().setText(TEXT).setWidth(WIDTH).setShouldCacheLayout(false).build(),
  )

  private companion object {
    const val TEXT = "שלום עולם\n\nמה שלומך היום"
    const val WIDTH = 300
    const val POOL_SIZE = 3
  }
}
