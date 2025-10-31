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

package com.facebook.fbui.textlayoutbuilder.util

import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [23])
@RunWith(RobolectricTestRunner::class)
class LayoutMeasureUtilTest {

  private lateinit var layout: Layout

  @Test
  fun testOneLineWithAdd() {
    layout = StaticLayoutHelper.makeStaticLayout(ONE_LINE_TEXT, 1.0f, 5.0f)
    assertThat(LayoutMeasureUtil.getHeight(layout)).isEqualTo(15)
  }

  @Test
  fun testTwoLinesWithAdd() {
    layout = StaticLayoutHelper.makeStaticLayout(TWO_LINE_TEXT, 1.0f, 5.0f)
    assertThat(LayoutMeasureUtil.getHeight(layout)).isEqualTo(30)
  }

  @Test
  fun testOneLineWithMulti() {
    layout = StaticLayoutHelper.makeStaticLayout(ONE_LINE_TEXT, 1.5f, 0.0f)
    assertThat(LayoutMeasureUtil.getHeight(layout)).isEqualTo(15)
  }

  @Test
  fun testTwoLinesWithMulti() {
    layout = StaticLayoutHelper.makeStaticLayout(TWO_LINE_TEXT, 1.5f, 0.0f)
    assertThat(LayoutMeasureUtil.getHeight(layout)).isEqualTo(30)
  }

  @Test
  fun testOneLineWithAddAndMulti() {
    layout = StaticLayoutHelper.makeStaticLayout(ONE_LINE_TEXT, 1.5f, 2.0f)
    assertThat(LayoutMeasureUtil.getHeight(layout)).isEqualTo(17)
  }

  @Test
  fun testTwoLinesWithAddAndMulti() {
    layout = StaticLayoutHelper.makeStaticLayout(TWO_LINE_TEXT, 1.5f, 2.0f)
    assertThat(LayoutMeasureUtil.getHeight(layout)).isEqualTo(34)
  }

  @Test
  fun testEmptyTextWithAddAndMulti() {
    layout = StaticLayoutHelper.makeStaticLayout("", 1.5f, 2.0f)
    assertThat(LayoutMeasureUtil.getHeight(layout)).isEqualTo(10)
  }

  private class DummyTextPaint : TextPaint() {
    override fun getFontMetricsInt(fmi: FontMetricsInt?): Int {
      fmi?.let { metrics ->
        metrics.ascent = 0
        metrics.top = 0
        metrics.descent = 10
        metrics.bottom = 10
      }
      return 0
    }
  }

  private object StaticLayoutHelper {
    fun makeStaticLayout(text: CharSequence, spacingMult: Float, spacingAdd: Float): Layout {
      return StaticLayout(
          text,
          DummyTextPaint(),
          1000,
          Layout.Alignment.ALIGN_NORMAL,
          spacingMult,
          spacingAdd,
          true,
      )
    }
  }

  companion object {
    private const val ONE_LINE_TEXT = "test"
    private const val TWO_LINE_TEXT = "test\ntest"
  }
}
