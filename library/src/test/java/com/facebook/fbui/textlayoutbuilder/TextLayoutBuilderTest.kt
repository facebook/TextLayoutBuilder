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
import org.assertj.core.api.Assertions.assertThat
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
    assertThat(mBuilder.text).isEqualTo("Android")
    assertThat(mLayout?.text).isEqualTo("Android")
  }

  @Test
  fun testSetTextNull() {
    mLayout = mBuilder.setText(null).build()
    assertThat(mBuilder.text).isNull()
    assertThat(mLayout).isNull()
  }

  @Test
  fun testSetTextEmptyStringWithZeroLengthTextAllowed() {
    mLayout = mBuilder.setText("").setShouldLayoutZeroLengthText(true).build()
    assertThat(mBuilder.text).isEqualTo("")
    assertThat(mLayout?.text).isEqualTo("")
  }

  @Test
  fun testSetTextEmptyStringWithZeroLengthTextNotAllowed() {
    mLayout = mBuilder.setText("").setShouldLayoutZeroLengthText(false).build()
    assertThat(mBuilder.text).isEqualTo("")
    assertThat(mLayout).isNull()
  }

  @Test
  fun testSetTextSize() {
    mLayout = mBuilder.setTextSize(10).build()
    assertThat(mBuilder.textSize).isEqualTo(10.0f)
    assertThat(mLayout?.paint?.textSize ?: 0f).isEqualTo(10.0f)
  }

  @Test
  fun testSetTextColor() {
    mLayout = mBuilder.setTextColor(0xFFFF0000.toInt()).build()
    assertThat(mBuilder.textColor).isEqualTo(0xFFFF0000.toInt())
    assertThat(mLayout?.paint?.color).isEqualTo(0xFFFF0000.toInt())
  }

  @Test
  fun testSetTextColorStateList() {
    mLayout = mBuilder.setTextColor(ColorStateList.valueOf(0xFFFF0000.toInt())).build()
    assertThat(mBuilder.textColor).isEqualTo(0xFFFF0000.toInt())
    assertThat(mLayout?.paint?.color).isEqualTo(0xFFFF0000.toInt())
  }

  @Test
  fun testSetLinkColor() {
    mLayout = mBuilder.setLinkColor(0xFFFF0000.toInt()).build()
    assertThat(mBuilder.linkColor).isEqualTo(0xFFFF0000.toInt())
    assertThat(mLayout?.paint?.linkColor).isEqualTo(0xFFFF0000.toInt())
  }

  @Test
  fun testSetTextSpacingExtra() {
    mLayout = mBuilder.setTextSpacingExtra(10f).build()
    assertThat(mBuilder.textSpacingExtra).isEqualTo(10.0f)
    assertThat(mLayout?.spacingAdd ?: 0f).isEqualTo(10.0f)
  }

  @Test
  fun testSetTextSpacingMultiplier() {
    mLayout = mBuilder.setTextSpacingMultiplier(1.5f).build()
    assertThat(mBuilder.textSpacingMultiplier).isEqualTo(1.5f)
    assertThat(mLayout?.spacingMultiplier ?: 0f).isEqualTo(1.5f)
  }

  @Test
  @TextLayoutMode(LEGACY)
  fun testSetTextLineHeight() {
    val lineHeight = 15f
    mLayout = mBuilder.setLineHeight(lineHeight).build()
    assertThat(mBuilder.lineHeight).isEqualTo(15f)
    assertThat(mLayout?.spacingMultiplier ?: 0f).isEqualTo(1.0f)
    val layout = mLayout
    if (layout != null) {
      assertThat(layout.spacingAdd).isEqualTo(lineHeight - layout.paint.getFontMetrics(null))
    }
  }

  @Test
  fun testSetIncludeFontPadding() {
    mLayout = mBuilder.setIncludeFontPadding(false).build()
    assertThat(mBuilder.includeFontPadding).isFalse()
  }

  @Test
  fun testSetAlignment() {
    mLayout = mBuilder.setAlignment(Layout.Alignment.ALIGN_CENTER).build()
    assertThat(mBuilder.alignment).isEqualTo(Layout.Alignment.ALIGN_CENTER)
    assertThat(mLayout?.alignment).isEqualTo(Layout.Alignment.ALIGN_CENTER)
  }

  @Test
  fun testSetTextDirection() {
    mLayout = mBuilder.setTextDirection(TextDirectionHeuristicsCompat.LOCALE).build()
    assertThat(mBuilder.textDirection).isEqualTo(TextDirectionHeuristicsCompat.LOCALE)
  }

  @Test
  fun testSetTypeface() {
    mLayout = mBuilder.setTypeface(Typeface.MONOSPACE).build()
    assertThat(mBuilder.typeface).isEqualTo(Typeface.MONOSPACE)
  }

  @Test
  fun testSetEllipsize() {
    mLayout = mBuilder.setEllipsize(TextUtils.TruncateAt.MARQUEE).build()
    assertThat(mBuilder.ellipsize).isEqualTo(TextUtils.TruncateAt.MARQUEE)
  }

  @Test
  fun testSetSingleLine() {
    mLayout = mBuilder.setSingleLine(true).build()
    assertThat(mBuilder.singleLine).isTrue()
  }

  @Test
  fun testSetMaxLines() {
    mLayout = mBuilder.setMaxLines(10).build()
    assertThat(mBuilder.maxLines).isEqualTo(10)
  }

  @Test
  fun testSetShouldCacheLayout() {
    mLayout = mBuilder.setShouldCacheLayout(false).build()
    assertThat(mBuilder.shouldCacheLayout).isFalse()
  }

  @Test
  fun testSetShouldWarmText() {
    mLayout = mBuilder.setShouldWarmText(true).build()
    assertThat(mBuilder.shouldWarmText).isTrue()
  }

  @Test
  fun testSetBreakStrategy() {
    mLayout = mBuilder.setBreakStrategy(1).build()
    assertThat(mBuilder.breakStrategy).isEqualTo(1)
  }

  @Test
  fun testSetHyphenationFrequency() {
    mLayout = mBuilder.setHyphenationFrequency(1).build()
    assertThat(mBuilder.hyphenationFrequency).isEqualTo(1)
  }

  @Config(sdk = [26])
  @TargetApi(26)
  @Test
  fun testSetJustificationMode() {
    mLayout = mBuilder.setJustificationMode(1).build()
    assertThat(mBuilder.justificationMode).isEqualTo(1)
  }

  @Test
  fun testSetLeftIndents() {
    val leftIndents = intArrayOf(0, 1)
    mLayout = mBuilder.setIndents(leftIndents, null).build()
    assertThat(mBuilder.leftIndents).isEqualTo(leftIndents)
  }

  @Test
  fun testSetRightIndents() {
    val rightIndents = intArrayOf(0, 1)
    mLayout = mBuilder.setIndents(null, rightIndents).build()
    assertThat(mBuilder.rightIndents).isEqualTo(rightIndents)
  }

  @Test
  fun testSetGlyphWarmer() {
    val glyphWarmer = FakeGlyphWarmer()
    mLayout = mBuilder.setGlyphWarmer(glyphWarmer).build()
    assertThat(mBuilder.glyphWarmer).isEqualTo(glyphWarmer)
  }

  // Test functionality.
  @Test
  fun testSingleLine() {
    mLayout = mBuilder.setText(LONG_TEXT).setSingleLine(true).setWidth(1000).build()
    assertThat(mLayout?.lineCount).isEqualTo(1)
  }

  @Test
  fun testMaxLines() {
    mLayout = mBuilder.setText(LONG_TEXT).setMaxLines(2).setWidth(1000).build()
    assertThat(mLayout?.lineCount).isEqualTo(2)
  }

  @Test
  fun testMinEms() {
    mBuilder.setText(LONG_TEXT).setMinEms(10).build()
    assertThat(mBuilder.minEms).isEqualTo(10)
    assertThat(mBuilder.minWidth).isEqualTo(-1)
  }

  @Test
  fun testMaxEms() {
    mBuilder.setText(LONG_TEXT).setMaxEms(10).build()
    assertThat(mBuilder.maxEms).isEqualTo(10)
    assertThat(mBuilder.maxWidth).isEqualTo(-1)
  }

  @Test
  fun testMinWidth() {
    mBuilder.setText(LONG_TEXT).setMinWidth(100).build()
    assertThat(mBuilder.minWidth).isEqualTo(100)
    assertThat(mBuilder.minEms).isEqualTo(-1)
  }

  @Test
  fun testMaxWidth() {
    mBuilder.setText(LONG_TEXT).setMaxWidth(100).build()
    assertThat(mBuilder.maxWidth).isEqualTo(100)
    assertThat(mBuilder.maxEms).isEqualTo(-1)
  }

  @Test
  fun testDensity() {
    mBuilder.setDensity(1.5f).build()
    assertThat(mBuilder.density).isEqualTo(1.5f)
  }

  @Test
  fun testDrawableState() {
    val drawableState = intArrayOf(0, 1)
    mLayout = mBuilder.setDrawableState(drawableState).build()
    assertThat(mBuilder.drawableState).containsExactly(*drawableState)
  }

  @Test
  fun testNewPaint() {
    val oldPaint = mBuilder.mParams.paint

    // Build the current builder.
    mBuilder.build()

    // Change paint properties.
    mBuilder.setShadowLayer(10.0f, 1.0f, 1.0f, 0)
    val newPaint = mBuilder.mParams.paint
    assertThat(newPaint).isNotEqualTo(oldPaint)
  }

  @Test
  fun testWarmText() {
    val warmer = FakeGlyphWarmer()
    mLayout = mBuilder.setShouldWarmText(true).setGlyphWarmer(warmer).build()
    assertThat(warmer.getLayout()).isEqualTo(mLayout)
  }

  @Test
  fun testDoNotWarmText() {
    val warmer = FakeGlyphWarmer()
    mLayout = mBuilder.setShouldWarmText(false).setGlyphWarmer(warmer).build()
    assertThat(warmer.getLayout()).isNull()
  }

  @Test
  fun testCaching() {
    mLayout = mBuilder.setShouldCacheLayout(true).build()
    val newLayout = mBuilder.build()
    assertThat(newLayout).isEqualTo(mLayout)
    assertThat(TextLayoutBuilder.sCache.size()).isEqualTo(1)
    assertThat(TextLayoutBuilder.sCache.get(mBuilder.mParams.hashCode())).isEqualTo(mLayout)
  }

  @Test
  fun testNoCaching() {
    mLayout = mBuilder.setShouldCacheLayout(false).build()
    val newLayout = mBuilder.build()
    assertThat(newLayout).isNotEqualTo(mLayout)
    assertThat(TextLayoutBuilder.sCache.size()).isEqualTo(0)
    assertThat(TextLayoutBuilder.sCache.get(mBuilder.mParams.hashCode())).isNull()
  }

  @Test
  fun testTwoBuildersWithSameParamsAndCaching() {
    mLayout = mBuilder.setShouldCacheLayout(true).build()

    val newBuilder = TextLayoutBuilder()
    val newLayout = newBuilder.setText(TEST).setShouldCacheLayout(true).build()
    assertThat(newLayout).isEqualTo(mLayout)
  }

  @Test
  fun testTwoBuildersWithSameParamsAndNoCaching() {
    mLayout = mBuilder.setShouldCacheLayout(false).build()

    val newBuilder = TextLayoutBuilder()
    val newLayout = newBuilder.setText(TEST).setShouldCacheLayout(false).build()
    assertThat(newLayout).isNotEqualTo(mLayout)
  }

  @Test
  fun testSpannableString() {
    val spannable = SpannableStringBuilder("This is a bold text")
    spannable.setSpan(StyleSpan(Typeface.BOLD), 10, 13, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    mLayout = mBuilder.setText(spannable).build()
    assertThat(mLayout?.text).isEqualTo(spannable)
  }

  @Test
  fun testCachingSpannableString() {
    val spannable = SpannableStringBuilder("This is a bold text")
    spannable.setSpan(StyleSpan(Typeface.BOLD), 10, 13, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    mLayout = mBuilder.setText(spannable).setShouldCacheLayout(true).build()
    assertThat(TextLayoutBuilder.sCache.size()).isEqualTo(1)
    assertThat(TextLayoutBuilder.sCache.get(mBuilder.mParams.hashCode())).isEqualTo(mLayout)
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
    assertThat(TextLayoutBuilder.sCache.size()).isEqualTo(0)
    assertThat(TextLayoutBuilder.sCache.get(mBuilder.mParams.hashCode())).isNull()
  }

  @Config(sdk = [23])
  @Test(expected = IllegalArgumentException::class)
  fun testNullSpansAreCaught() {
    val ssb = SpannableStringBuilder().append("abcd", null, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    mBuilder.setText(ssb).build()
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
