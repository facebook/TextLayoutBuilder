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

import android.annotation.TargetApi
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.core.text.TextDirectionHeuristicsCompat
import com.facebook.fbui.textlayoutbuilder.shadows.ShadowPicture
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.TextLayoutMode
import org.robolectric.annotation.TextLayoutMode.Mode.LEGACY

/** Tests [TextLayoutBuilder] */
@Config(sdk = [23], shadows = [ShadowPicture::class])
@RunWith(RobolectricTestRunner::class)
class TextLayoutBuilderTest {

  private lateinit var builder: TextLayoutBuilder
  private var layout: Layout? = null

  @Before
  fun setup() {
    builder = TextLayoutBuilder()
    builder.setText(TEST)

    // Clear the cache.
    TextLayoutBuilder.cache.evictAll()
  }

  // Test setters.
  @Test
  fun testSetText() {
    layout = builder.setText("Android").build()
    assertEquals(builder.text, "Android")
    assertEquals(layout?.text, "Android")
  }

  @Test
  fun testSetTextNull() {
    layout = builder.setText(null).build()
    assertEquals(builder.text, null)
    assertEquals(layout, null)
  }

  @Test
  fun testSetTextEmptyStringWithZeroLengthTextAllowed() {
    layout = builder.setText("").setShouldLayoutZeroLengthText(true).build()
    assertEquals(builder.text, "")
    assertEquals(layout?.text, "")
  }

  @Test
  fun testSetTextEmptyStringWithZeroLengthTextNotAllowed() {
    layout = builder.setText("").setShouldLayoutZeroLengthText(false).build()
    assertEquals(builder.text, "")
    assertEquals(layout, null)
  }

  @Test
  fun testSetTextSize() {
    layout = builder.setTextSize(10).build()
    assertEquals(builder.textSize, 10.0f, 0.0f)
    assertEquals(layout?.paint?.textSize ?: 0f, 10.0f, 0.0f)
  }

  @Test
  fun testSetTextColor() {
    layout = builder.setTextColor(0xFFFF0000.toInt()).build()
    assertEquals(builder.textColor, 0xFFFF0000.toInt())
    assertEquals(layout?.paint?.color, 0xFFFF0000.toInt())
  }

  @Test
  fun testSetTextColorStateList() {
    layout = builder.setTextColor(ColorStateList.valueOf(0xFFFF0000.toInt())).build()
    assertEquals(builder.textColor, 0xFFFF0000.toInt())
    assertEquals(layout?.paint?.color, 0xFFFF0000.toInt())
  }

  @Test
  fun testSetLinkColor() {
    layout = builder.setLinkColor(0xFFFF0000.toInt()).build()
    assertEquals(builder.linkColor, 0xFFFF0000.toInt())
    assertEquals(layout?.paint?.linkColor, 0xFFFF0000.toInt())
  }

  @Test
  fun testSetTextSpacingExtra() {
    layout = builder.setTextSpacingExtra(10f).build()
    assertEquals(builder.textSpacingExtra, 10.0f, 0.0f)
    assertEquals(layout?.spacingAdd ?: 0f, 10.0f, 0.0f)
  }

  @Test
  fun testSetTextSpacingMultiplier() {
    layout = builder.setTextSpacingMultiplier(1.5f).build()
    assertEquals(builder.textSpacingMultiplier, 1.5f, 0.0f)
    assertEquals(layout?.spacingMultiplier ?: 0f, 1.5f, 0.0f)
  }

  @Test
  @TextLayoutMode(LEGACY)
  fun testSetTextLineHeight() {
    val lineHeight = 15f
    layout = builder.setLineHeight(lineHeight).build()
    assertEquals(builder.lineHeight, 15f, 0.0f)
    assertEquals(layout?.spacingMultiplier ?: 0f, 1.0f, 0.0f)
    val layout = layout
    if (layout != null) {
      assertEquals(layout.spacingAdd, lineHeight - layout.paint.getFontMetrics(null), 0.0f)
    }
  }

  @Test
  fun testSetIncludeFontPadding() {
    layout = builder.setIncludeFontPadding(false).build()
    assertEquals(builder.includeFontPadding, false)
  }

  @Test
  fun testSetAlignment() {
    layout = builder.setAlignment(Layout.Alignment.ALIGN_CENTER).build()
    assertEquals(builder.alignment, Layout.Alignment.ALIGN_CENTER)
    assertEquals(layout?.alignment, Layout.Alignment.ALIGN_CENTER)
  }

  @Test
  fun testSetTextDirection() {
    layout = builder.setTextDirection(TextDirectionHeuristicsCompat.LOCALE).build()
    assertEquals(builder.textDirection, TextDirectionHeuristicsCompat.LOCALE)
  }

  @Test
  fun testSetTypeface() {
    layout = builder.setTypeface(Typeface.MONOSPACE).build()
    assertEquals(builder.typeface, Typeface.MONOSPACE)
  }

  @Test
  fun testSetEllipsize() {
    layout = builder.setEllipsize(TextUtils.TruncateAt.MARQUEE).build()
    assertEquals(builder.ellipsize, TextUtils.TruncateAt.MARQUEE)
  }

  @Test
  fun testSetSingleLine() {
    layout = builder.setSingleLine(true).build()
    assertEquals(builder.singleLine, true)
  }

  @Test
  fun testSetMaxLines() {
    layout = builder.setMaxLines(10).build()
    assertEquals(builder.maxLines, 10)
  }

  @Test
  fun testSetShouldCacheLayout() {
    layout = builder.setShouldCacheLayout(false).build()
    assertEquals(builder.shouldCacheLayout, false)
  }

  @Test
  fun testSetShouldWarmText() {
    layout = builder.setShouldWarmText(true).build()
    assertEquals(builder.shouldWarmText, true)
  }

  @Test
  fun testSetBreakStrategy() {
    layout = builder.setBreakStrategy(1).build()
    assertEquals(builder.breakStrategy, 1)
  }

  @Test
  fun testSetHyphenationFrequency() {
    layout = builder.setHyphenationFrequency(1).build()
    assertEquals(builder.hyphenationFrequency, 1)
  }

  @Config(sdk = [26])
  @TargetApi(26)
  @Test
  fun testSetJustificationMode() {
    layout = builder.setJustificationMode(1).build()
    assertEquals(builder.justificationMode, 1)
  }

  @Test
  fun testSetLeftIndents() {
    val leftIndents = intArrayOf(0, 1)
    layout = builder.setIndents(leftIndents, null).build()
    assertEquals(builder.leftIndents, leftIndents)
  }

  @Test
  fun testSetRightIndents() {
    val rightIndents = intArrayOf(0, 1)
    layout = builder.setIndents(null, rightIndents).build()
    assertEquals(builder.rightIndents, rightIndents)
  }

  @Test
  fun testSetGlyphWarmer() {
    val glyphWarmer = FakeGlyphWarmer()
    layout = builder.setGlyphWarmer(glyphWarmer).build()
    assertEquals(builder.glyphWarmer, glyphWarmer)
  }

  // Test functionality.
  @Test
  fun testSingleLine() {
    layout = builder.setText(LONG_TEXT).setSingleLine(true).setWidth(1000).build()
    assertEquals(layout?.lineCount, 1)
  }

  @Test
  fun testMaxLines() {
    layout = builder.setText(LONG_TEXT).setMaxLines(2).setWidth(1000).build()
    assertEquals(layout?.lineCount, 2)
  }

  @Test
  fun testMinEms() {
    builder.setText(LONG_TEXT).setMinEms(10).build()
    assertEquals(builder.minEms, 10)
    assertEquals(builder.minWidth, -1)
  }

  @Test
  fun testMaxEms() {
    builder.setText(LONG_TEXT).setMaxEms(10).build()
    assertEquals(builder.maxEms, 10)
    assertEquals(builder.maxWidth, -1)
  }

  @Test
  fun testMinWidth() {
    builder.setText(LONG_TEXT).setMinWidth(100).build()
    assertEquals(builder.minWidth, 100)
    assertEquals(builder.minEms, -1)
  }

  @Test
  fun testMaxWidth() {
    builder.setText(LONG_TEXT).setMaxWidth(100).build()
    assertEquals(builder.maxWidth, 100)
    assertEquals(builder.maxEms, -1)
  }

  @Test
  fun testDensity() {
    builder.setDensity(1.5f).build()
    assertEquals(builder.density, 1.5f, 0.001f)
  }

  @Test
  fun testDrawableState() {
    val drawableState = intArrayOf(0, 1)
    layout = builder.setDrawableState(drawableState).build()
    assertArrayEquals(builder.drawableState, drawableState)
  }

  @Test
  fun testNewPaint() {
    val oldPaint = builder.params.paint

    // Build the current builder.
    builder.build()

    // Change paint properties.
    builder.setShadowLayer(10.0f, 1.0f, 1.0f, 0)
    val newPaint = builder.params.paint
    assertNotEquals(oldPaint, newPaint)
  }

  @Test
  fun testWarmText() {
    val warmer = FakeGlyphWarmer()
    layout = builder.setShouldWarmText(true).setGlyphWarmer(warmer).build()
    assertEquals(warmer.getLayout(), layout)
  }

  @Test
  fun testDoNotWarmText() {
    val warmer = FakeGlyphWarmer()
    layout = builder.setShouldWarmText(false).setGlyphWarmer(warmer).build()
    assertEquals(warmer.getLayout(), null)
  }

  @Test
  fun testCaching() {
    layout = builder.setShouldCacheLayout(true).build()
    val newLayout = builder.build()
    assertEquals(layout, newLayout)
    assertEquals(TextLayoutBuilder.cache.size(), 1)
    assertEquals(TextLayoutBuilder.cache.get(builder.params.hashCode()), layout)
  }

  @Test
  fun testNoCaching() {
    layout = builder.setShouldCacheLayout(false).build()
    val newLayout = builder.build()
    assertNotEquals(layout, newLayout)
    assertEquals(TextLayoutBuilder.cache.size(), 0)
    assertEquals(TextLayoutBuilder.cache.get(builder.params.hashCode()), null)
  }

  @Test
  fun testTwoBuildersWithSameParamsAndCaching() {
    layout = builder.setShouldCacheLayout(true).build()

    val newBuilder = TextLayoutBuilder()
    val newLayout = newBuilder.setText(TEST).setShouldCacheLayout(true).build()
    assertEquals(layout, newLayout)
  }

  @Test
  fun testTwoBuildersWithSameParamsAndNoCaching() {
    layout = builder.setShouldCacheLayout(false).build()

    val newBuilder = TextLayoutBuilder()
    val newLayout = newBuilder.setText(TEST).setShouldCacheLayout(false).build()
    assertNotEquals(layout, newLayout)
  }

  @Test
  fun testSpannableString() {
    val spannable = SpannableStringBuilder("This is a bold text")
    spannable.setSpan(StyleSpan(Typeface.BOLD), 10, 13, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    layout = builder.setText(spannable).build()
    assertEquals(layout?.text, spannable)
  }

  @Test
  fun testCachingSpannableString() {
    val spannable = SpannableStringBuilder("This is a bold text")
    spannable.setSpan(StyleSpan(Typeface.BOLD), 10, 13, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    layout = builder.setText(spannable).setShouldCacheLayout(true).build()
    assertEquals(TextLayoutBuilder.cache.size(), 1)
    assertEquals(TextLayoutBuilder.cache.get(builder.params.hashCode()), layout)
  }

  @Test
  fun testNoCachingSpannableString() {
    val clickableSpan =
        object : ClickableSpan() {
          override fun onClick(widget: View) {
            // Do nothing.
          }
        }

    val spannable = SpannableStringBuilder("This is a bold text")
    spannable.setSpan(clickableSpan, 10, 13, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    layout = builder.setText(spannable).setShouldCacheLayout(true).build()
    assertEquals(TextLayoutBuilder.cache.size(), 0)
    assertEquals(TextLayoutBuilder.cache.get(builder.params.hashCode()), null)
  }

  @Config(sdk = [23])
  @Test(expected = IllegalArgumentException::class)
  fun testNullSpansAreCaught() {
    val ssb = SpannableStringBuilder().append("abcd", null, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    builder.setText(ssb).build()
  }

  private class FakeGlyphWarmer : GlyphWarmer {
    private var layout: Layout? = null

    override fun warmLayout(layout: Layout?) {
      this.layout = layout
    }

    fun getLayout(): Layout? {
      return layout
    }
  }

  companion object {
    private const val TEST = "TEST"
    private const val LONG_TEXT =
        "Lorem ipsum dolor sit amet test \n" +
            "Lorem ipsum dolor sit amet test \n" +
            "Lorem ipsum dolor sit amet test \n" +
            "Lorem ipsum dolor sit amet test \n"
  }
}
