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

  private lateinit var mBuilder: TextLayoutBuilder
  private var mLayout: Layout? = null

  @Before
  fun setup() {
    mBuilder = TextLayoutBuilder()
    mBuilder.setText(TEST)

    // Clear the cache.
    TextLayoutBuilder.sCache.evictAll()
  }

  // Test setters.
  @Test
  fun testSetText() {
    mLayout = mBuilder.setText("Android").build()
    assertEquals(mBuilder.text, "Android")
    assertEquals(mLayout?.text, "Android")
  }

  @Test
  fun testSetTextNull() {
    mLayout = mBuilder.setText(null).build()
    assertEquals(mBuilder.text, null)
    assertEquals(mLayout, null)
  }

  @Test
  fun testSetTextEmptyStringWithZeroLengthTextAllowed() {
    mLayout = mBuilder.setText("").setShouldLayoutZeroLengthText(true).build()
    assertEquals(mBuilder.text, "")
    assertEquals(mLayout?.text, "")
  }

  @Test
  fun testSetTextEmptyStringWithZeroLengthTextNotAllowed() {
    mLayout = mBuilder.setText("").setShouldLayoutZeroLengthText(false).build()
    assertEquals(mBuilder.text, "")
    assertEquals(mLayout, null)
  }

  @Test
  fun testSetTextSize() {
    mLayout = mBuilder.setTextSize(10).build()
    assertEquals(mBuilder.textSize, 10.0f, 0.0f)
    assertEquals(mLayout?.paint?.textSize ?: 0f, 10.0f, 0.0f)
  }

  @Test
  fun testSetTextColor() {
    mLayout = mBuilder.setTextColor(0xFFFF0000.toInt()).build()
    assertEquals(mBuilder.textColor, 0xFFFF0000.toInt())
    assertEquals(mLayout?.paint?.color, 0xFFFF0000.toInt())
  }

  @Test
  fun testSetTextColorStateList() {
    mLayout = mBuilder.setTextColor(ColorStateList.valueOf(0xFFFF0000.toInt())).build()
    assertEquals(mBuilder.textColor, 0xFFFF0000.toInt())
    assertEquals(mLayout?.paint?.color, 0xFFFF0000.toInt())
  }

  @Test
  fun testSetLinkColor() {
    mLayout = mBuilder.setLinkColor(0xFFFF0000.toInt()).build()
    assertEquals(mBuilder.linkColor, 0xFFFF0000.toInt())
    assertEquals(mLayout?.paint?.linkColor, 0xFFFF0000.toInt())
  }

  @Test
  fun testSetTextSpacingExtra() {
    mLayout = mBuilder.setTextSpacingExtra(10f).build()
    assertEquals(mBuilder.textSpacingExtra, 10.0f, 0.0f)
    assertEquals(mLayout?.spacingAdd ?: 0f, 10.0f, 0.0f)
  }

  @Test
  fun testSetTextSpacingMultiplier() {
    mLayout = mBuilder.setTextSpacingMultiplier(1.5f).build()
    assertEquals(mBuilder.textSpacingMultiplier, 1.5f, 0.0f)
    assertEquals(mLayout?.spacingMultiplier ?: 0f, 1.5f, 0.0f)
  }

  @Test
  @TextLayoutMode(LEGACY)
  fun testSetTextLineHeight() {
    val lineHeight = 15f
    mLayout = mBuilder.setLineHeight(lineHeight).build()
    assertEquals(mBuilder.lineHeight, 15f, 0.0f)
    assertEquals(mLayout?.spacingMultiplier ?: 0f, 1.0f, 0.0f)
    val layout = mLayout
    if (layout != null) {
      assertEquals(layout.spacingAdd, lineHeight - layout.paint.getFontMetrics(null), 0.0f)
    }
  }

  @Test
  fun testSetIncludeFontPadding() {
    mLayout = mBuilder.setIncludeFontPadding(false).build()
    assertEquals(mBuilder.includeFontPadding, false)
  }

  @Test
  fun testSetAlignment() {
    mLayout = mBuilder.setAlignment(Layout.Alignment.ALIGN_CENTER).build()
    assertEquals(mBuilder.alignment, Layout.Alignment.ALIGN_CENTER)
    assertEquals(mLayout?.alignment, Layout.Alignment.ALIGN_CENTER)
  }

  @Test
  fun testSetTextDirection() {
    mLayout = mBuilder.setTextDirection(TextDirectionHeuristicsCompat.LOCALE).build()
    assertEquals(mBuilder.textDirection, TextDirectionHeuristicsCompat.LOCALE)
  }

  @Test
  fun testSetTypeface() {
    mLayout = mBuilder.setTypeface(Typeface.MONOSPACE).build()
    assertEquals(mBuilder.typeface, Typeface.MONOSPACE)
  }

  @Test
  fun testSetEllipsize() {
    mLayout = mBuilder.setEllipsize(TextUtils.TruncateAt.MARQUEE).build()
    assertEquals(mBuilder.ellipsize, TextUtils.TruncateAt.MARQUEE)
  }

  @Test
  fun testSetSingleLine() {
    mLayout = mBuilder.setSingleLine(true).build()
    assertEquals(mBuilder.singleLine, true)
  }

  @Test
  fun testSetMaxLines() {
    mLayout = mBuilder.setMaxLines(10).build()
    assertEquals(mBuilder.maxLines, 10)
  }

  @Test
  fun testSetShouldCacheLayout() {
    mLayout = mBuilder.setShouldCacheLayout(false).build()
    assertEquals(mBuilder.shouldCacheLayout, false)
  }

  @Test
  fun testSetShouldWarmText() {
    mLayout = mBuilder.setShouldWarmText(true).build()
    assertEquals(mBuilder.shouldWarmText, true)
  }

  @Test
  fun testSetBreakStrategy() {
    mLayout = mBuilder.setBreakStrategy(1).build()
    assertEquals(mBuilder.breakStrategy, 1)
  }

  @Test
  fun testSetHyphenationFrequency() {
    mLayout = mBuilder.setHyphenationFrequency(1).build()
    assertEquals(mBuilder.hyphenationFrequency, 1)
  }

  @Config(sdk = [26])
  @TargetApi(26)
  @Test
  fun testSetJustificationMode() {
    mLayout = mBuilder.setJustificationMode(1).build()
    assertEquals(mBuilder.justificationMode, 1)
  }

  @Test
  fun testSetLeftIndents() {
    val leftIndents = intArrayOf(0, 1)
    mLayout = mBuilder.setIndents(leftIndents, null).build()
    assertEquals(mBuilder.leftIndents, leftIndents)
  }

  @Test
  fun testSetRightIndents() {
    val rightIndents = intArrayOf(0, 1)
    mLayout = mBuilder.setIndents(null, rightIndents).build()
    assertEquals(mBuilder.rightIndents, rightIndents)
  }

  @Test
  fun testSetGlyphWarmer() {
    val glyphWarmer = FakeGlyphWarmer()
    mLayout = mBuilder.setGlyphWarmer(glyphWarmer).build()
    assertEquals(mBuilder.glyphWarmer, glyphWarmer)
  }

  // Test functionality.
  @Test
  fun testSingleLine() {
    mLayout = mBuilder.setText(LONG_TEXT).setSingleLine(true).setWidth(1000).build()
    assertEquals(mLayout?.lineCount, 1)
  }

  @Test
  fun testMaxLines() {
    mLayout = mBuilder.setText(LONG_TEXT).setMaxLines(2).setWidth(1000).build()
    assertEquals(mLayout?.lineCount, 2)
  }

  @Test
  fun testMinEms() {
    mBuilder.setText(LONG_TEXT).setMinEms(10).build()
    assertEquals(mBuilder.minEms, 10)
    assertEquals(mBuilder.minWidth, -1)
  }

  @Test
  fun testMaxEms() {
    mBuilder.setText(LONG_TEXT).setMaxEms(10).build()
    assertEquals(mBuilder.maxEms, 10)
    assertEquals(mBuilder.maxWidth, -1)
  }

  @Test
  fun testMinWidth() {
    mBuilder.setText(LONG_TEXT).setMinWidth(100).build()
    assertEquals(mBuilder.minWidth, 100)
    assertEquals(mBuilder.minEms, -1)
  }

  @Test
  fun testMaxWidth() {
    mBuilder.setText(LONG_TEXT).setMaxWidth(100).build()
    assertEquals(mBuilder.maxWidth, 100)
    assertEquals(mBuilder.maxEms, -1)
  }

  @Test
  fun testDensity() {
    mBuilder.setDensity(1.5f).build()
    assertEquals(mBuilder.density, 1.5f, 0.001f)
  }

  @Test
  fun testDrawableState() {
    val drawableState = intArrayOf(0, 1)
    mLayout = mBuilder.setDrawableState(drawableState).build()
    assertArrayEquals(mBuilder.drawableState, drawableState)
  }

  @Test
  fun testNewPaint() {
    val oldPaint = mBuilder.mParams.paint

    // Build the current builder.
    mBuilder.build()

    // Change paint properties.
    mBuilder.setShadowLayer(10.0f, 1.0f, 1.0f, 0)
    val newPaint = mBuilder.mParams.paint
    assertNotEquals(oldPaint, newPaint)
  }

  @Test
  fun testWarmText() {
    val warmer = FakeGlyphWarmer()
    mLayout = mBuilder.setShouldWarmText(true).setGlyphWarmer(warmer).build()
    assertEquals(warmer.getLayout(), mLayout)
  }

  @Test
  fun testDoNotWarmText() {
    val warmer = FakeGlyphWarmer()
    mLayout = mBuilder.setShouldWarmText(false).setGlyphWarmer(warmer).build()
    assertEquals(warmer.getLayout(), null)
  }

  @Test
  fun testCaching() {
    mLayout = mBuilder.setShouldCacheLayout(true).build()
    val newLayout = mBuilder.build()
    assertEquals(mLayout, newLayout)
    assertEquals(TextLayoutBuilder.sCache.size(), 1)
    assertEquals(TextLayoutBuilder.sCache.get(mBuilder.mParams.hashCode()), mLayout)
  }

  @Test
  fun testNoCaching() {
    mLayout = mBuilder.setShouldCacheLayout(false).build()
    val newLayout = mBuilder.build()
    assertNotEquals(mLayout, newLayout)
    assertEquals(TextLayoutBuilder.sCache.size(), 0)
    assertEquals(TextLayoutBuilder.sCache.get(mBuilder.mParams.hashCode()), null)
  }

  @Test
  fun testTwoBuildersWithSameParamsAndCaching() {
    mLayout = mBuilder.setShouldCacheLayout(true).build()

    val newBuilder = TextLayoutBuilder()
    val newLayout = newBuilder.setText(TEST).setShouldCacheLayout(true).build()
    assertEquals(mLayout, newLayout)
  }

  @Test
  fun testTwoBuildersWithSameParamsAndNoCaching() {
    mLayout = mBuilder.setShouldCacheLayout(false).build()

    val newBuilder = TextLayoutBuilder()
    val newLayout = newBuilder.setText(TEST).setShouldCacheLayout(false).build()
    assertNotEquals(mLayout, newLayout)
  }

  @Test
  fun testSpannableString() {
    val spannable = SpannableStringBuilder("This is a bold text")
    spannable.setSpan(StyleSpan(Typeface.BOLD), 10, 13, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    mLayout = mBuilder.setText(spannable).build()
    assertEquals(mLayout?.text, spannable)
  }

  @Test
  fun testCachingSpannableString() {
    val spannable = SpannableStringBuilder("This is a bold text")
    spannable.setSpan(StyleSpan(Typeface.BOLD), 10, 13, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    mLayout = mBuilder.setText(spannable).setShouldCacheLayout(true).build()
    assertEquals(TextLayoutBuilder.sCache.size(), 1)
    assertEquals(TextLayoutBuilder.sCache.get(mBuilder.mParams.hashCode()), mLayout)
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
    mLayout = mBuilder.setText(spannable).setShouldCacheLayout(true).build()
    assertEquals(TextLayoutBuilder.sCache.size(), 0)
    assertEquals(TextLayoutBuilder.sCache.get(mBuilder.mParams.hashCode()), null)
  }

  @Config(sdk = [23])
  @Test(expected = IllegalArgumentException::class)
  fun testNullSpansAreCaught() {
    val ssb = SpannableStringBuilder().append("abcd", null, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    mBuilder.setText(ssb).build()
  }

  private class FakeGlyphWarmer : GlyphWarmer {
    private var mLayout: Layout? = null

    override fun warmLayout(layout: Layout) {
      mLayout = layout
    }

    fun getLayout(): Layout? {
      return mLayout
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
