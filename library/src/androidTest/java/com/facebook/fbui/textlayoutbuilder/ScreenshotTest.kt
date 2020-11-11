/*
 * Copyright (c) Facebook, Inc. and its affiliates.
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
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.facebook.testing.screenshot.Screenshot
import com.facebook.testing.screenshot.ScreenshotRunner
import com.facebook.testing.screenshot.ViewHelpers
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScreenshotTest {
  companion object {
    const val DENSITY = 2f // 320dpi

    val paragraph =
        """
    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut rhoncus, odio a hendrerit
    porttitor, tellus purus rhoncus massa, sit amet varius urna arcu non elit. Quisque rutrum
    lacinia tempus. Mauris quis sapien nulla. Nam iaculis nec nibh eget posuere. Quisque sodales
    interdum risus, eget commodo augue interdum ac. Integer libero eros, pellentesque eu maximus
    at, tempus eu arcu. Duis sed elit auctor, imperdiet neque quis, viverra augue. In iaculis
    eleifend arcu quis vulputate. Aliquam lorem ligula, condimentum sed odio non, aliquam blandit
    turpis. Nullam vitae egestas leo, nec blandit dui. Morbi at ultrices justo, eu tempus lorem.
    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec tempus ante et sapien vehicula,
    id ornare enim pharetra. Vestibulum interdum erat lorem, a consectetur ex rhoncus eget.
    """.trimIndent()

    @BeforeClass
    @JvmStatic
    fun before() {
      ScreenshotRunner.onCreate(
          InstrumentationRegistry.getInstrumentation(), InstrumentationRegistry.getArguments())
    }

    @AfterClass
    @JvmStatic
    fun after() {
      ScreenshotRunner.onDestroy()
    }
  }

  @Test
  fun testDefault() {
    val context = InstrumentationRegistry.getTargetContext()
    val layout = TextLayoutBuilder().setText(paragraph).setDensity(DENSITY).build()!!
    val view = TestView(context, layout)

    ViewHelpers.setupView(view)
        .setExactWidthPx(layout.width)
        .setExactHeightPx(layout.height)
        .layout()
    Screenshot.snap(view).record()
  }

  @Test
  fun testAbsoluteSizeSpan() {
    val context = InstrumentationRegistry.getTargetContext()
    val stringBuilder = SpannableStringBuilder("BIG TEXT half size text")
    stringBuilder.setSpan(AbsoluteSizeSpan(24, true), 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    val layout =
        TextLayoutBuilder()
            .setText(stringBuilder)
            .setTextSize(dp(12f))
            .setDensity(DENSITY)
            .build()!!
    val view = TestView(context, layout)

    ViewHelpers.setupView(view)
        .setExactWidthPx(layout.width)
        .setExactHeightPx(layout.height)
        .layout()
    Screenshot.snap(view).record()
  }

  @Test
  fun testCenterAlignment() {
    val context = InstrumentationRegistry.getTargetContext()
    val layout =
        TextLayoutBuilder()
            .setText(paragraph)
            .setTextSize(dp(12f))
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .build()!!
    val view = TestView(context, layout)

    ViewHelpers.setupView(view)
        .setExactWidthPx(layout.width)
        .setExactHeightPx(layout.height)
        .layout()
    Screenshot.snap(view).record()
  }

  @Test
  fun testLineHeight() {
    val context = InstrumentationRegistry.getTargetContext()
    val layout =
        TextLayoutBuilder()
            .setText(paragraph)
            .setTextSize(dp(12f))
            .setLineHeight(dp(20f).toFloat())
            .build()!!
    val view = TestView(context, layout)

    ViewHelpers.setupView(view)
        .setExactWidthPx(layout.width)
        .setExactHeightPx(layout.height)
        .layout()
    Screenshot.snap(view).record()
  }

  private fun dp(value: Float): Int {
    return (value * DENSITY + 0.5f).toInt()
  }
}
