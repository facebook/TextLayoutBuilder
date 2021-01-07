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

package com.facebook.fbui.textlayoutbuilder;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.robolectric.annotation.TextLayoutMode.Mode.LEGACY;

import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.View;
import androidx.core.text.TextDirectionHeuristicsCompat;
import com.facebook.fbui.textlayoutbuilder.shadows.ShadowPicture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.TextLayoutMode;

/** Tests {@link TextLayoutBuilder} */
@Config(
    sdk = 21,
    shadows = {ShadowPicture.class})
@RunWith(RobolectricTestRunner.class)
public class TextLayoutBuilderTest {

  private static final String TEST = "TEST";
  private static final String LONG_TEXT =
      "Lorem ipsum dolor sit amet test \n"
          + "Lorem ipsum dolor sit amet test \n"
          + "Lorem ipsum dolor sit amet test \n"
          + "Lorem ipsum dolor sit amet test \n";

  private TextLayoutBuilder mBuilder;
  private Layout mLayout;

  @Before
  public void setup() {
    mBuilder = new TextLayoutBuilder();
    mBuilder.setText(TEST);

    // Clear the cache.
    TextLayoutBuilder.sCache.evictAll();
  }

  // Test setters.
  @Test
  public void testSetText() {
    mLayout = mBuilder.setText("Android").build();
    assertEquals(mBuilder.getText(), "Android");
    assertEquals(mLayout.getText(), "Android");
  }

  @Test
  public void testSetTextNull() {
    mLayout = mBuilder.setText(null).build();
    assertEquals(mBuilder.getText(), null);
    assertEquals(mLayout, null);
  }

  @Test
  public void testSetTextEmptyStringWithZeroLengthTextAllowed() {
    mLayout = mBuilder.setText("").setShouldLayoutZeroLengthText(true).build();
    assertEquals(mBuilder.getText(), "");
    assertEquals(mLayout.getText(), "");
  }

  @Test
  public void testSetTextEmptyStringWithZeroLengthTextNotAllowed() {
    mLayout = mBuilder.setText("").setShouldLayoutZeroLengthText(false).build();
    assertEquals(mBuilder.getText(), "");
    assertEquals(mLayout, null);
  }

  @Test
  public void testSetTextSize() {
    mLayout = mBuilder.setTextSize(10).build();
    assertEquals(mBuilder.getTextSize(), 10.0f, 0.0f);
    assertEquals(mLayout.getPaint().getTextSize(), 10.0f, 0.0f);
  }

  @Test
  public void testSetTextColor() {
    mLayout = mBuilder.setTextColor(0xFFFF0000).build();
    assertEquals(mBuilder.getTextColor(), 0xFFFF0000);
    assertEquals(mLayout.getPaint().getColor(), 0xFFFF0000);
  }

  @Test
  public void testSetTextColorStateList() {
    mLayout = mBuilder.setTextColor(ColorStateList.valueOf(0xFFFF0000)).build();
    assertEquals(mBuilder.getTextColor(), 0xFFFF0000);
    assertEquals(mLayout.getPaint().getColor(), 0xFFFF0000);
  }

  @Test
  public void testSetLinkColor() {
    mLayout = mBuilder.setLinkColor(0xFFFF0000).build();
    assertEquals(mBuilder.getLinkColor(), 0xFFFF0000);
    assertEquals(mLayout.getPaint().linkColor, 0xFFFF0000);
  }

  @Test
  public void testSetTextSpacingExtra() {
    mLayout = mBuilder.setTextSpacingExtra(10).build();
    assertEquals(mBuilder.getTextSpacingExtra(), 10.0f, 0.0f);
    assertEquals(mLayout.getSpacingAdd(), 10.0f, 0.0f);
  }

  @Test
  public void testSetTextSpacingMultiplier() {
    mLayout = mBuilder.setTextSpacingMultiplier(1.5f).build();
    assertEquals(mBuilder.getTextSpacingMultiplier(), 1.5f, 0.0f);
    assertEquals(mLayout.getSpacingMultiplier(), 1.5f, 0.0f);
  }

  @Test
  @TextLayoutMode(LEGACY)
  public void testSetTextLineHeight() {
    final float lineHeight = 15f;
    mLayout = mBuilder.setLineHeight(lineHeight).build();
    assertEquals(mBuilder.getLineHeight(), 15f, 0.0f);
    assertEquals(mLayout.getSpacingMultiplier(), 1.0f, 0.0f);
    assertEquals(
        mLayout.getSpacingAdd(), lineHeight - mLayout.getPaint().getFontMetrics(null), 0.0f);
  }

  @Test
  public void testSetIncludeFontPadding() {
    mLayout = mBuilder.setIncludeFontPadding(false).build();
    assertEquals(mBuilder.getIncludeFontPadding(), false);
  }

  @Test
  public void testSetAlignment() {
    mLayout = mBuilder.setAlignment(Layout.Alignment.ALIGN_CENTER).build();
    assertEquals(mBuilder.getAlignment(), Layout.Alignment.ALIGN_CENTER);
    assertEquals(mLayout.getAlignment(), Layout.Alignment.ALIGN_CENTER);
  }

  @Test
  public void testSetTextDirection() {
    mLayout = mBuilder.setTextDirection(TextDirectionHeuristicsCompat.LOCALE).build();
    assertEquals(mBuilder.getTextDirection(), TextDirectionHeuristicsCompat.LOCALE);
  }

  @Test
  public void testSetTypeface() {
    mLayout = mBuilder.setTypeface(Typeface.MONOSPACE).build();
    assertEquals(mBuilder.getTypeface(), Typeface.MONOSPACE);
  }

  @Test
  public void testSetEllipsize() {
    mLayout = mBuilder.setEllipsize(TextUtils.TruncateAt.MARQUEE).build();
    assertEquals(mBuilder.getEllipsize(), TextUtils.TruncateAt.MARQUEE);
  }

  @Test
  public void testSetSingleLine() {
    mLayout = mBuilder.setSingleLine(true).build();
    assertEquals(mBuilder.getSingleLine(), true);
  }

  @Test
  public void testSetMaxLines() {
    mLayout = mBuilder.setMaxLines(10).build();
    assertEquals(mBuilder.getMaxLines(), 10.0f, 0.0f);
  }

  @Test
  public void testSetShouldCacheLayout() {
    mLayout = mBuilder.setShouldCacheLayout(false).build();
    assertEquals(mBuilder.getShouldCacheLayout(), false);
  }

  @Test
  public void testSetShouldWarmText() {
    mLayout = mBuilder.setShouldWarmText(true).build();
    assertEquals(mBuilder.getShouldWarmText(), true);
  }

  @Test
  public void testSetBreakStrategy() {
    mLayout = mBuilder.setBreakStrategy(1).build();
    assertEquals(mBuilder.getBreakStrategy(), 1);
  }

  @Test
  public void testSetHyphenationFrequency() {
    mLayout = mBuilder.setHyphenationFrequency(1).build();
    assertEquals(mBuilder.getHyphenationFrequency(), 1);
  }

  @Test
  public void testSetJustificationMode() {
    mLayout = mBuilder.setJustificationMode(1).build();
    assertEquals(mBuilder.getJustificationMode(), 1);
  }

  @Test
  public void testSetLeftIndents() {
    int[] leftIndents = new int[] {0, 1};
    mLayout = mBuilder.setIndents(leftIndents, null).build();
    assertEquals(mBuilder.getLeftIndents(), leftIndents);
  }

  @Test
  public void testSetRightIndents() {
    int[] rightIndents = new int[] {0, 1};
    mLayout = mBuilder.setIndents(null, rightIndents).build();
    assertEquals(mBuilder.getRightIndents(), rightIndents);
  }

  @Test
  public void testSetGlyphWarmer() {
    FakeGlyphWarmer glyphWarmer = new FakeGlyphWarmer();
    mLayout = mBuilder.setGlyphWarmer(glyphWarmer).build();
    assertEquals(mBuilder.getGlyphWarmer(), glyphWarmer);
  }

  // Test functionality.
  @Test
  public void testSingleLine() {
    mLayout = mBuilder.setText(LONG_TEXT).setSingleLine(true).setWidth(1000).build();
    assertEquals(mLayout.getLineCount(), 1);
  }

  @Test
  public void testMaxLines() {
    mLayout = mBuilder.setText(LONG_TEXT).setMaxLines(2).setWidth(1000).build();
    assertEquals(mLayout.getLineCount(), 2);
  }

  @Test
  public void testMinEms() {
    mBuilder.setText(LONG_TEXT).setMinEms(10).build();
    assertEquals(mBuilder.getMinEms(), 10);
    assertEquals(mBuilder.getMinWidth(), -1);
  }

  @Test
  public void testMaxEms() {
    mBuilder.setText(LONG_TEXT).setMaxEms(10).build();
    assertEquals(mBuilder.getMaxEms(), 10);
    assertEquals(mBuilder.getMaxWidth(), -1);
  }

  @Test
  public void testMinWidth() {
    mBuilder.setText(LONG_TEXT).setMinWidth(100).build();
    assertEquals(mBuilder.getMinWidth(), 100);
    assertEquals(mBuilder.getMinEms(), -1);
  }

  @Test
  public void testMaxWidth() {
    mBuilder.setText(LONG_TEXT).setMaxWidth(100).build();
    assertEquals(mBuilder.getMaxWidth(), 100);
    assertEquals(mBuilder.getMaxEms(), -1);
  }

  @Test
  public void testDensity() {
    mBuilder.setDensity(1.5f).build();
    assertEquals(mBuilder.getDensity(), 1.5f, 0.001f);
  }

  @Test
  public void testDrawableState() {
    int[] drawableState = {0, 1};
    mLayout = mBuilder.setDrawableState(drawableState).build();
    assertArrayEquals(mBuilder.getDrawableState(), drawableState);
  }

  @Test
  public void testNewPaint() {
    Paint oldPaint = mBuilder.mParams.paint;

    // Build the current builder.
    mBuilder.build();

    // Change paint properties.
    mBuilder.setShadowLayer(10.0f, 1.0f, 1.0f, 0);
    Paint newPaint = mBuilder.mParams.paint;
    assertNotEquals(oldPaint, newPaint);
  }

  @Test
  public void testWarmText() {
    FakeGlyphWarmer warmer = new FakeGlyphWarmer();
    mLayout = mBuilder.setShouldWarmText(true).setGlyphWarmer(warmer).build();
    assertEquals(warmer.getLayout(), mLayout);
  }

  @Test
  public void testDoNotWarmText() {
    FakeGlyphWarmer warmer = new FakeGlyphWarmer();
    mLayout = mBuilder.setShouldWarmText(false).setGlyphWarmer(warmer).build();
    assertEquals(warmer.getLayout(), null);
  }

  @Test
  public void testCaching() {
    mLayout = mBuilder.setShouldCacheLayout(true).build();
    Layout newLayout = mBuilder.build();
    assertEquals(mLayout, newLayout);
    assertEquals(mBuilder.sCache.size(), 1);
    assertEquals(mBuilder.sCache.get(mBuilder.mParams.hashCode()), mLayout);
  }

  @Test
  public void testNoCaching() {
    mLayout = mBuilder.setShouldCacheLayout(false).build();
    Layout newLayout = mBuilder.build();
    assertNotEquals(mLayout, newLayout);
    assertEquals(mBuilder.sCache.size(), 0);
    assertEquals(mBuilder.sCache.get(mBuilder.mParams.hashCode()), null);
  }

  @Test
  public void testTwoBuildersWithSameParamsAndCaching() {
    mLayout = mBuilder.setShouldCacheLayout(true).build();

    TextLayoutBuilder newBuilder = new TextLayoutBuilder();
    Layout newLayout = newBuilder.setText(TEST).setShouldCacheLayout(true).build();
    assertEquals(mLayout, newLayout);
  }

  @Test
  public void testTwoBuildersWithSameParamsAndNoCaching() {
    mLayout = mBuilder.setShouldCacheLayout(false).build();

    TextLayoutBuilder newBuilder = new TextLayoutBuilder();
    Layout newLayout = newBuilder.setText(TEST).setShouldCacheLayout(false).build();
    assertNotEquals(mLayout, newLayout);
  }

  @Test
  public void testSpannableString() {
    SpannableStringBuilder spannable = new SpannableStringBuilder("This is a bold text");
    spannable.setSpan(new StyleSpan(Typeface.BOLD), 10, 13, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
    mLayout = mBuilder.setText(spannable).build();
    assertEquals(mLayout.getText(), spannable);
  }

  @Test
  public void testCachingSpannableString() {
    SpannableStringBuilder spannable = new SpannableStringBuilder("This is a bold text");
    spannable.setSpan(new StyleSpan(Typeface.BOLD), 10, 13, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
    mLayout = mBuilder.setText(spannable).setShouldCacheLayout(true).build();
    assertEquals(mBuilder.sCache.size(), 1);
    assertEquals(mBuilder.sCache.get(mBuilder.mParams.hashCode()), mLayout);
  }

  @Test
  public void testNoCachingSpannableString() {
    ClickableSpan clickableSpan =
        new ClickableSpan() {
          @Override
          public void onClick(View widget) {
            // Do nothing.
          }
        };

    SpannableStringBuilder spannable = new SpannableStringBuilder("This is a bold text");
    spannable.setSpan(clickableSpan, 10, 13, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
    mLayout = mBuilder.setText(spannable).setShouldCacheLayout(true).build();
    assertEquals(mBuilder.sCache.size(), 0);
    assertEquals(mBuilder.sCache.get(mBuilder.mParams.hashCode()), null);
  }

  @Config(sdk = 21)
  @Test(expected = IllegalArgumentException.class)
  public void testNullSpansAreCaught() {
    SpannableStringBuilder ssb =
        new SpannableStringBuilder().append("abcd", null, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    mBuilder.setText(ssb).build();
  }

  private static class FakeGlyphWarmer implements GlyphWarmer {
    private Layout mLayout = null;

    @Override
    public void warmLayout(Layout layout) {
      mLayout = layout;
    }

    Layout getLayout() {
      return mLayout;
    }
  }
}
