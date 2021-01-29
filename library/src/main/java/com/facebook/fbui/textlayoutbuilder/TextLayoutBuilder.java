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

import static android.text.Layout.Alignment;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.text.BoringLayout;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Log;
import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.RequiresApi;
import androidx.annotation.VisibleForTesting;
import androidx.collection.LruCache;
import androidx.core.text.TextDirectionHeuristicCompat;
import androidx.core.text.TextDirectionHeuristicsCompat;
import java.lang.annotation.Retention;
import java.util.Arrays;

/**
 * An utility class to create text {@link Layout}s easily.
 *
 * <p>This class uses a Builder pattern to allow re-using the same object to create text {@link
 * Layout}s with similar properties.
 */
public class TextLayoutBuilder {

  /**
   * Measure mode constants similar to {@link android.view.View.MeasureSpec}
   *
   * @see #MEASURE_MODE_UNSPECIFIED
   * @see #MEASURE_MODE_EXACTLY
   * @see #MEASURE_MODE_AT_MOST
   */
  @Retention(SOURCE)
  @IntDef({MEASURE_MODE_UNSPECIFIED, MEASURE_MODE_EXACTLY, MEASURE_MODE_AT_MOST})
  public @interface MeasureMode {}

  public static final int MEASURE_MODE_UNSPECIFIED = 0;
  public static final int MEASURE_MODE_EXACTLY = 1;
  public static final int MEASURE_MODE_AT_MOST = 2;

  // Default maxLines.
  public static final int DEFAULT_MAX_LINES = Integer.MAX_VALUE;

  private static final float DEFAULT_SPACING_ADD = 0.0f;
  private static final float DEFAULT_SPACING_MULT = 1.0f;
  private static final float DEFAULT_LINE_HEIGHT = Float.MAX_VALUE;

  private static final int EMS = 1;
  private static final int PIXELS = 2;

  private int mMinWidth = 0;
  private int mMinWidthMode = PIXELS;
  private int mMaxWidth = Integer.MAX_VALUE;
  private int mMaxWidthMode = PIXELS;

  // Cache for text layouts.
  @VisibleForTesting static final LruCache<Integer, Layout> sCache = new LruCache<>(100);

  /** Params for creating the layout. */
  @VisibleForTesting
  static class Params {
    TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    float mShadowDx;
    float mShadowDy;
    float mShadowRadius;
    int mShadowColor;
    int width;
    @MeasureMode int measureMode;

    CharSequence text;
    @Nullable ColorStateList color;

    float spacingMult = DEFAULT_SPACING_MULT;
    float spacingAdd = DEFAULT_SPACING_ADD;
    float lineHeight = DEFAULT_LINE_HEIGHT;
    boolean includePadding = true;
    boolean useLineSpacingFromFallbacks = Build.VERSION.SDK_INT >= 28;
    boolean shouldLayoutZeroLengthText = false;

    @Nullable TextUtils.TruncateAt ellipsize = null;
    boolean singleLine = false;
    int maxLines = DEFAULT_MAX_LINES;
    Alignment alignment = Alignment.ALIGN_NORMAL;
    TextDirectionHeuristicCompat textDirection = TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR;

    int breakStrategy = 0;
    int hyphenationFrequency = 0;
    int justificationMode = 0; // JUSTIFICATION_MODE_NONE
    int[] leftIndents;
    int[] rightIndents;

    boolean mForceNewPaint = false;

    /** Create a new paint after the builder builds for the first time. */
    void createNewPaintIfNeeded() {
      // Once after build() is called, it is not safe to set properties
      // on the paint as we cache the text layouts.
      // Hence we create a new paint object,
      // if we ever change one of the paint's properties.
      if (mForceNewPaint) {
        TextPaint newPaint = new TextPaint(paint);
        newPaint.set(paint);
        paint = newPaint;
        mForceNewPaint = false;
      }
    }

    int getLineHeight() {
      return Math.round(paint.getFontMetricsInt(null) * spacingMult + spacingAdd);
    }

    @Override
    public int hashCode() {
      int hashCode = 1;

      // Hashing the TextPaint object
      hashCode = 31 * hashCode + paint.getColor();
      hashCode = 31 * hashCode + Float.floatToIntBits(paint.getTextSize());
      hashCode = 31 * hashCode + (paint.getTypeface() != null ? paint.getTypeface().hashCode() : 0);
      hashCode = 31 * hashCode + Float.floatToIntBits(mShadowDx);
      hashCode = 31 * hashCode + Float.floatToIntBits(mShadowDy);
      hashCode = 31 * hashCode + Float.floatToIntBits(mShadowRadius);
      hashCode = 31 * hashCode + mShadowColor;
      hashCode = 31 * hashCode + paint.linkColor;
      hashCode = 31 * hashCode + Float.floatToIntBits(paint.density);
      hashCode = 31 * hashCode + Arrays.hashCode(paint.drawableState);

      hashCode = 31 * hashCode + width;
      hashCode = 31 * hashCode + measureMode;
      hashCode = 31 * hashCode + Float.floatToIntBits(spacingMult);
      hashCode = 31 * hashCode + Float.floatToIntBits(spacingAdd);
      hashCode = 31 * hashCode + Float.floatToIntBits(lineHeight);
      hashCode = 31 * hashCode + (includePadding ? 1 : 0);
      hashCode = 31 * hashCode + (useLineSpacingFromFallbacks ? 1 : 0);
      hashCode = 31 * hashCode + (ellipsize != null ? ellipsize.hashCode() : 0);
      hashCode = 31 * hashCode + (singleLine ? 1 : 0);
      hashCode = 31 * hashCode + maxLines;
      hashCode = 31 * hashCode + (alignment != null ? alignment.hashCode() : 0);
      hashCode = 31 * hashCode + (textDirection != null ? textDirection.hashCode() : 0);
      hashCode = 31 * hashCode + breakStrategy;
      hashCode = 31 * hashCode + hyphenationFrequency;
      hashCode = 31 * hashCode + Arrays.hashCode(leftIndents);
      hashCode = 31 * hashCode + Arrays.hashCode(rightIndents);
      hashCode = 31 * hashCode + (text != null ? text.hashCode() : 0);

      return hashCode;
    }
  }

  // Params for the builder.
  @VisibleForTesting final Params mParams = new Params();

  // Locally cached layout for an instance.
  private @Nullable Layout mSavedLayout = null;

  // Text layout glyph warmer.
  private GlyphWarmer mGlyphWarmer;

  // Cache layout or not.
  private boolean mShouldCacheLayout = true;

  // Warm layout or not.
  private boolean mShouldWarmText = false;

  /**
   * Sets the intended width of the text layout.
   *
   * @param width The width of the text layout
   * @return This {@link TextLayoutBuilder} instance
   * @see #setWidth(int, int)
   */
  public TextLayoutBuilder setWidth(@Px int width) {
    return setWidth(width, width <= 0 ? MEASURE_MODE_UNSPECIFIED : MEASURE_MODE_EXACTLY);
  }

  /**
   * Sets the intended width of the text layout while respecting the measure mode.
   *
   * @param width The width of the text layout
   * @param measureMode The mode with which to treat the given width
   * @return This {@link TextLayoutBuilder} instance
   * @see #setWidth(int)
   */
  public TextLayoutBuilder setWidth(@Px int width, @MeasureMode int measureMode) {
    if (mParams.width != width || mParams.measureMode != measureMode) {
      mParams.width = width;
      mParams.measureMode = measureMode;
      mSavedLayout = null;
    }
    return this;
  }

  /**
   * Returns the text that would be packed in a layout by this TextLayoutBuilder.
   *
   * @return The text used by this TextLayoutBuilder
   */
  public CharSequence getText() {
    return mParams.text;
  }

  /**
   * Sets the text for the layout.
   *
   * @param text The text for the layout
   * @return This {@link TextLayoutBuilder} instance
   */
  public TextLayoutBuilder setText(@Nullable CharSequence text) {
    if (text == mParams.text) {
      return this;
    }

    if (Build.VERSION.SDK_INT >= 21 && text instanceof SpannableStringBuilder) {
      // Workaround for https://issuetracker.google.com/issues/117666255
      // We cannot use getSpans here because it omits null spans, even though they exist
      // So instead we just execute the bug repro and catch the NPE
      try {
        text.hashCode();
      } catch (NullPointerException e) {
        throw new IllegalArgumentException(
            "The given text contains a null span. Due to an Android framework bug, this will cause an exception later down the line.",
            e);
      }
    }

    if (text != null && text.equals(mParams.text)) {
      return this;
    }

    mParams.text = text;
    mSavedLayout = null;
    return this;
  }

  /**
   * Returns the text size for this TextLayoutBuilder.
   *
   * @return The text size used by this TextLayoutBuilder
   */
  public float getTextSize() {
    return mParams.paint.getTextSize();
  }

  /**
   * Sets the text size for the layout.
   *
   * @param size The text size in pixels
   * @return This {@link TextLayoutBuilder} instance
   */
  public TextLayoutBuilder setTextSize(int size) {
    if (mParams.paint.getTextSize() != size) {
      mParams.createNewPaintIfNeeded();
      mParams.paint.setTextSize(size);
      mSavedLayout = null;
    }
    return this;
  }

  /**
   * Returns the text color for this TextLayoutBuilder.
   *
   * @return The text color used by this TextLayoutBuilder
   */
  @ColorInt
  public int getTextColor() {
    return mParams.paint.getColor();
  }

  /**
   * Sets the text color for the layout.
   *
   * @param color The text color for the layout
   * @return This {@link TextLayoutBuilder} instance
   */
  public TextLayoutBuilder setTextColor(@ColorInt int color) {
    mParams.createNewPaintIfNeeded();
    mParams.color = null;
    mParams.paint.setColor(color);
    mSavedLayout = null;
    return this;
  }

  /**
   * Sets the text color for the layout.
   *
   * @param colorStateList The text color state list for the layout
   * @return This {@link TextLayoutBuilder} instance
   */
  public TextLayoutBuilder setTextColor(ColorStateList colorStateList) {
    mParams.createNewPaintIfNeeded();
    mParams.color = colorStateList;
    mParams.paint.setColor(mParams.color != null ? mParams.color.getDefaultColor() : Color.BLACK);
    mSavedLayout = null;
    return this;
  }

  /**
   * Returns the link color for this TextLayoutBuilder.
   *
   * @return The link color used by this TextLayoutBuilder
   */
  @ColorInt
  public int getLinkColor() {
    return mParams.paint.linkColor;
  }

  /**
   * Sets the link color for the text in the layout.
   *
   * @param linkColor The link color
   * @return This {@link TextLayoutBuilder} instance
   */
  public TextLayoutBuilder setLinkColor(@ColorInt int linkColor) {
    if (mParams.paint.linkColor != linkColor) {
      mParams.createNewPaintIfNeeded();
      mParams.paint.linkColor = linkColor;
      mSavedLayout = null;
    }
    return this;
  }

  /**
   * Returns the text spacing extra for this TextLayoutBuilder.
   *
   * @return The text spacing extra used by this TextLayoutBuilder
   */
  public float getTextSpacingExtra() {
    return mParams.spacingAdd;
  }

  /**
   * Sets the text extra spacing for the layout. Extra spacing will be ignored if {@link
   * TextLayoutBuilder#setLineHeight(float)} was used to set an explicit line height.
   *
   * @param spacingExtra the extra space that is added to the height of each line
   * @return This {@link TextLayoutBuilder} instance
   */
  public TextLayoutBuilder setTextSpacingExtra(float spacingExtra) {
    if (mParams.lineHeight == DEFAULT_LINE_HEIGHT && mParams.spacingAdd != spacingExtra) {
      mParams.spacingAdd = spacingExtra;
      mSavedLayout = null;
    }
    return this;
  }

  /**
   * Returns the text spacing multiplier for this TextLayoutBuilder.
   *
   * @return The text spacing multiplier used by this TextLayoutBuilder
   */
  public float getTextSpacingMultiplier() {
    return mParams.spacingMult;
  }

  /**
   * Sets the line spacing multiplier for the layout. Line spacing multiplier will be ignored if
   * {@link TextLayoutBuilder#setLineHeight(float)} was used to set an explicit line height.
   *
   * @param spacingMultiplier the value by which each line's height is multiplied
   * @return This {@link TextLayoutBuilder} instance
   */
  public TextLayoutBuilder setTextSpacingMultiplier(float spacingMultiplier) {
    if (mParams.lineHeight == DEFAULT_LINE_HEIGHT && mParams.spacingMult != spacingMultiplier) {
      mParams.spacingMult = spacingMultiplier;
      mSavedLayout = null;
    }
    return this;
  }

  /**
   * Returns the line height for this TextLayoutBuilder.
   *
   * @return The line height used by this TextLayoutBuilder
   */
  public float getLineHeight() {
    return mParams.getLineHeight();
  }

  /**
   * Sets the line height for this layout.
   *
   * <p>This mimics the behavior of
   * https://developer.android.com/reference/android/widget/TextView.html#setLineHeight(int) and
   * should not be used with {@link TextLayoutBuilder#setTextSpacingExtra(float)} or {@link
   * TextLayoutBuilder#setTextSpacingMultiplier(float)}.
   *
   * @param lineHeight the line height between two lines of text in px.
   * @return This {@link TextLayoutBuilder} instance.
   */
  public TextLayoutBuilder setLineHeight(float lineHeight) {
    if (mParams.lineHeight != lineHeight) {
      mParams.lineHeight = lineHeight;
      mParams.spacingAdd = lineHeight - mParams.paint.getFontMetrics(null);
      mParams.spacingMult = 1.0f;
      mSavedLayout = null;
    }
    return this;
  }

  /**
   * Gets the text letter-space value, which determines the spacing between characters. The value
   * returned is in ems. Normally, this value is 0.0.
   *
   * @return The text letter-space value in ems.
   * @see #setLetterSpacing(float)
   */
  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public float getLetterSpacing() {
    return mParams.paint.getLetterSpacing();
  }

  /**
   * Sets text letter-spacing in em units. Typical values for slight expansion will be around 0.05.
   * Negative values tighten text.
   *
   * @param letterSpacing A text letter-space value in ems.
   * @see #getLetterSpacing()
   */
  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public TextLayoutBuilder setLetterSpacing(float letterSpacing) {
    if (getLetterSpacing() != letterSpacing) {
      mParams.createNewPaintIfNeeded();
      mParams.paint.setLetterSpacing(letterSpacing);
      mSavedLayout = null;
    }
    return this;
  }

  /**
   * Returns whether this TextLayoutBuilder should include font padding.
   *
   * @return Whether this TextLayoutBuilder should include font padding
   */
  public boolean getIncludeFontPadding() {
    return mParams.includePadding;
  }

  /**
   * Set whether the text Layout includes extra top and bottom padding to make room for accents that
   * go above the normal ascent and descent.
   *
   * <p>The default is true.
   *
   * @param shouldInclude Whether to include font padding or not
   * @return This {@link TextLayoutBuilder} instance
   */
  public TextLayoutBuilder setIncludeFontPadding(boolean shouldInclude) {
    if (mParams.includePadding != shouldInclude) {
      mParams.includePadding = shouldInclude;
      mSavedLayout = null;
    }
    return this;
  }

  /**
   * Returns the text alignment for this TextLayoutBuilder.
   *
   * @return The text alignment used by this TextLayoutBuilder
   */
  public Alignment getAlignment() {
    return mParams.alignment;
  }

  /**
   * Sets text alignment for the layout.
   *
   * @param alignment The text alignment for the layout
   * @return This {@link TextLayoutBuilder} instance
   */
  public TextLayoutBuilder setAlignment(Alignment alignment) {
    if (mParams.alignment != alignment) {
      mParams.alignment = alignment;
      mSavedLayout = null;
    }
    return this;
  }

  /**
   * Returns the text direction for this TextLayoutBuilder.
   *
   * @return The text direction used by this TextLayoutBuilder
   */
  public TextDirectionHeuristicCompat getTextDirection() {
    return mParams.textDirection;
  }

  /**
   * Sets the text direction heuristic for the layout.
   *
   * <p>TextDirectionHeuristicCompat describes how to evaluate the text of this Layout to know
   * whether to use RTL or LTR text direction
   *
   * @param textDirection The text direction heuristic for the layout
   * @return This {@link TextLayoutBuilder} instance
   */
  public TextLayoutBuilder setTextDirection(TextDirectionHeuristicCompat textDirection) {
    if (mParams.textDirection != textDirection) {
      mParams.textDirection = textDirection;
      mSavedLayout = null;
    }
    return this;
  }

  /**
   * Sets the shadow layer for the layout.
   *
   * @param radius The radius of the blur for shadow
   * @param dx The horizontal translation of the origin
   * @param dy The vertical translation of the origin
   * @param color The shadow color
   * @return This {@link TextLayoutBuilder} instance
   */
  public TextLayoutBuilder setShadowLayer(float radius, float dx, float dy, @ColorInt int color) {
    mParams.createNewPaintIfNeeded();
    mParams.mShadowRadius = radius;
    mParams.mShadowDx = dx;
    mParams.mShadowDy = dy;
    mParams.mShadowColor = color;
    mParams.paint.setShadowLayer(radius, dx, dy, color);
    mSavedLayout = null;
    return this;
  }

  /**
   * Sets a text style for the layout.
   *
   * @param style The text style for the layout
   * @return This {@link TextLayoutBuilder} instance
   */
  public TextLayoutBuilder setTextStyle(int style) {
    return setTypeface(Typeface.defaultFromStyle(style));
  }

  /**
   * Returns the typeface for this TextLayoutBuilder.
   *
   * @return The typeface used by this TextLayoutBuilder
   */
  public Typeface getTypeface() {
    return mParams.paint.getTypeface();
  }

  /**
   * Sets the typeface used by this TextLayoutBuilder.
   *
   * @param typeface The typeface for this TextLayoutBuilder
   * @return This {@link TextLayoutBuilder} instance
   */
  public TextLayoutBuilder setTypeface(@Nullable Typeface typeface) {
    if (mParams.paint.getTypeface() != typeface) {
      mParams.createNewPaintIfNeeded();
      mParams.paint.setTypeface(typeface);
      mSavedLayout = null;
    }
    return this;
  }

  /**
   * Returns the drawable state for this TextLayoutBuilder.
   *
   * @return The drawable state used by this TextLayoutBuilder
   */
  public int[] getDrawableState() {
    return mParams.paint.drawableState;
  }

  /**
   * Updates the text colors based on the drawable state.
   *
   * @param drawableState The current drawable state of the View holding this layout
   * @return This {@link TextLayoutBuilder} instance
   */
  public TextLayoutBuilder setDrawableState(int[] drawableState) {
    mParams.createNewPaintIfNeeded();
    mParams.paint.drawableState = drawableState;

    if (mParams.color != null && mParams.color.isStateful()) {
      int color = mParams.color.getColorForState(drawableState, 0);
      mParams.paint.setColor(color);
      mSavedLayout = null;
    }
    return this;
  }

  /**
   * Returns the text ellipsize for this TextLayoutBuilder.
   *
   * @return The text ellipsize used by this TextLayoutBuilder
   */
  @Nullable
  public TextUtils.TruncateAt getEllipsize() {
    return mParams.ellipsize;
  }

  /**
   * Sets the ellipsis location for the layout.
   *
   * @param ellipsize The ellipsis location in the layout
   * @return This {@link TextLayoutBuilder} instance
   */
  public TextLayoutBuilder setEllipsize(@Nullable TextUtils.TruncateAt ellipsize) {
    if (mParams.ellipsize != ellipsize) {
      mParams.ellipsize = ellipsize;
      mSavedLayout = null;
    }
    return this;
  }

  /**
   * Set whether to respect the ascent and descent of the fallback fonts that are used in displaying
   * the text (which is needed to avoid text from consecutive lines running into each other).
   *
   * <p>If set, fallback fonts that end up getting used can increase the ascent and descent of the
   * lines that they are used on.
   *
   * <p>This behavior is defaulted to true on API >= 28 and false otherwise.
   */
  @RequiresApi(api = 28)
  public TextLayoutBuilder setUseLineSpacingFromFallbacks(boolean status) {
    if (mParams.useLineSpacingFromFallbacks != status) {
      mParams.useLineSpacingFromFallbacks = status;
      mSavedLayout = null;
    }
    return this;
  }

  /**
   * Returns whether to use line spacing from fallback fonts or not. See {@link
   * #setUseLineSpacingFromFallbacks}
   */
  public boolean getUseLineSpacingFromFallbacks() {
    return mParams.useLineSpacingFromFallbacks;
  }

  /**
   * Returns whether the TextLayoutBuilder should show a single line.
   *
   * @return Whether the TextLayoutBuilder should show a single line or not
   */
  public boolean getSingleLine() {
    return mParams.singleLine;
  }

  /**
   * Sets whether the text should be in a single line or not.
   *
   * @param singleLine Whether the text should be in a single line or not
   * @return This {@link TextLayoutBuilder} instance
   * @see #setMaxLines(int)
   */
  public TextLayoutBuilder setSingleLine(boolean singleLine) {
    if (mParams.singleLine != singleLine) {
      mParams.singleLine = singleLine;
      mSavedLayout = null;
    }
    return this;
  }

  /**
   * Returns the number of max lines used by this TextLayoutBuilder.
   *
   * @return The number of max lines for this TextLayoutBuilder
   */
  public int getMaxLines() {
    return mParams.maxLines;
  }

  /**
   * Sets a maximum number of lines to be shown by the Layout.
   *
   * <p>Note: Gingerbread always default to two lines max when ellipsized. This cannot be changed.
   * Use a TextView if you want more control over the number of lines.
   *
   * @param maxLines The number of maxLines to show in this Layout
   * @return This {@link TextLayoutBuilder} instance
   * @see #setSingleLine(boolean)
   */
  public TextLayoutBuilder setMaxLines(int maxLines) {
    if (mParams.maxLines != maxLines) {
      mParams.maxLines = maxLines;
      mSavedLayout = null;
    }
    return this;
  }

  /**
   * Returns the break strategy for this TextLayoutBuilder.
   *
   * @return The break strategy for this TextLayoutBuilder
   */
  public int getBreakStrategy() {
    return mParams.breakStrategy;
  }

  /**
   * Sets a break strategy breaking paragraphs into lines.
   *
   * @param breakStrategy The break strategy for breaking paragraphs into lines
   * @return This {@link TextLayoutBuilder} instance
   */
  public TextLayoutBuilder setBreakStrategy(int breakStrategy) {
    if (mParams.breakStrategy != breakStrategy) {
      mParams.breakStrategy = breakStrategy;
      mSavedLayout = null;
    }
    return this;
  }

  /**
   * Returns the hyphenation frequency for this TextLayoutBuilder.
   *
   * @return The hyphenation frequency for this TextLayoutBuilder
   */
  public int getHyphenationFrequency() {
    return mParams.hyphenationFrequency;
  }

  /**
   * Sets the frequency of automatic hyphenation to use when determining word breaks.
   *
   * @param hyphenationFrequency The frequency of automatic hyphenation to use for word breaks
   * @return This {@link TextLayoutBuilder} instance
   */
  public TextLayoutBuilder setHyphenationFrequency(int hyphenationFrequency) {
    if (mParams.hyphenationFrequency != hyphenationFrequency) {
      mParams.hyphenationFrequency = hyphenationFrequency;
      if (Build.VERSION.SDK_INT >= 23) {
        mSavedLayout = null;
      }
    }
    return this;
  }

  /**
   * Returns the left indents set on this TextLayoutBuilder.
   *
   * @return The left indents set on this TextLayoutBuilder
   */
  public int[] getLeftIndents() {
    return mParams.leftIndents;
  }

  /**
   * Returns the right indents set on this TextLayoutBuilder.
   *
   * @return The right indents set on this TextLayoutBuilder
   */
  public int[] getRightIndents() {
    return mParams.rightIndents;
  }

  /**
   * Sets the left and right indents for this TextLayoutBuilder.
   *
   * <p>The arrays hold an indent amount, one per line, measured in pixels. For lines past the last
   * element in the array, the last element repeats.
   *
   * @param leftIndents The left indents for the paragraph
   * @param rightIndents The left indents for the paragraph
   * @return This {@link TextLayoutBuilder} instance
   */
  public TextLayoutBuilder setIndents(int[] leftIndents, int[] rightIndents) {
    mParams.leftIndents = leftIndents;
    mParams.rightIndents = rightIndents;
    mSavedLayout = null;
    return this;
  }

  /**
   * Returns whether the TextLayoutBuilder should cache the layout.
   *
   * @return Whether the TextLayoutBuilder should cache the layout
   */
  public boolean getShouldCacheLayout() {
    return mShouldCacheLayout;
  }

  /**
   * Sets whether the text layout should be cached or not.
   *
   * <p>Note: If the Layout contains {@link ClickableSpan}s, the layout will not be cached.
   *
   * @param shouldCacheLayout True to cache the text layout, false otherwise
   * @return This {@link TextLayoutBuilder} instance
   */
  public TextLayoutBuilder setShouldCacheLayout(boolean shouldCacheLayout) {
    mShouldCacheLayout = shouldCacheLayout;
    return this;
  }

  /**
   * Returns whether the TextLayoutBuilder should warm the layout.
   *
   * @return Whether the TextLayoutBuilder should warm the layout
   */
  public boolean getShouldWarmText() {
    return mShouldWarmText;
  }

  /**
   * Sets whether the text should be warmed or not.
   *
   * <p>Note: Setting this true is highly effective for large blurbs of text. This method has to be
   * called before the draw pass.
   *
   * @param shouldWarmText True to warm the text layout, false otherwise
   * @return This {@link TextLayoutBuilder} instance
   * @see #setGlyphWarmer(GlyphWarmer)
   */
  public TextLayoutBuilder setShouldWarmText(boolean shouldWarmText) {
    mShouldWarmText = shouldWarmText;
    return this;
  }

  /**
   * Returns the GlyphWarmer used by the TextLayoutBuilder.
   *
   * @return The GlyphWarmer for this TextLayoutBuilder
   */
  public GlyphWarmer getGlyphWarmer() {
    return mGlyphWarmer;
  }

  /**
   * Sets the glyph warmer to use.
   *
   * @param glyphWarmer GlyphWarmer to use to warm the text layout
   * @return This {@link TextLayoutBuilder} instance
   * @see #setShouldWarmText(boolean)
   */
  public TextLayoutBuilder setGlyphWarmer(GlyphWarmer glyphWarmer) {
    mGlyphWarmer = glyphWarmer;
    return this;
  }

  /**
   * Returns the min width expressed in ems.
   *
   * @return the min width expressed in ems or -1
   * @see #setMinEms(int)
   */
  public int getMinEms() {
    return mMinWidthMode == EMS ? mMinWidth : -1;
  }

  /**
   * Sets the min width expressed in ems.
   *
   * @param minEms min width expressed in ems
   * @return This {@link TextLayoutBuilder} instance
   * @see #setMaxEms(int)
   * @see #setMinWidth(int)
   */
  public TextLayoutBuilder setMinEms(int minEms) {
    mMinWidth = minEms;
    mMinWidthMode = EMS;
    return this;
  }

  /**
   * Returns the min width expressed in pixels.
   *
   * @return the min width expressed in pixels or -1, if the min width was set in ems instead
   * @see #setMinWidth(int)
   */
  @Px
  public int getMinWidth() {
    return mMinWidthMode == PIXELS ? mMinWidth : -1;
  }

  /**
   * Sets the min width expressed in pixels.
   *
   * @param minWidth min width expressed in pixels.
   * @return This {@link TextLayoutBuilder} instance
   * @see #setMaxWidth(int)
   * @see #setMinEms(int)
   */
  public TextLayoutBuilder setMinWidth(@Px int minWidth) {
    mMinWidth = minWidth;
    mMinWidthMode = PIXELS;
    return this;
  }

  /**
   * Returns the max width expressed in ems.
   *
   * @return the max width expressed in ems or -1, if max width is set in pixels instead
   * @see #setMaxEms(int)
   */
  public int getMaxEms() {
    return mMaxWidthMode == EMS ? mMaxWidth : -1;
  }

  /**
   * Sets the max width expressed in ems.
   *
   * @param maxEms max width expressed in ems
   * @return This {@link TextLayoutBuilder} instance
   * @see #setMaxWidth(int)
   * @see #setMinEms(int)
   */
  public TextLayoutBuilder setMaxEms(int maxEms) {
    mMaxWidth = maxEms;
    mMaxWidthMode = EMS;
    return this;
  }

  /**
   * Returns the max width expressed in pixels.
   *
   * @return the max width expressed in pixels or -1, if the max width was set in ems instead
   * @see #setMaxWidth(int)
   */
  @Px
  public int getMaxWidth() {
    return mMaxWidthMode == PIXELS ? mMaxWidth : -1;
  }

  /**
   * Sets the max width expressed in pixels.
   *
   * @param maxWidth max width expressed in pixels
   * @return This {@link TextLayoutBuilder} instance
   * @see #setMaxEms(int)
   * @see #setMinWidth(int)
   */
  public TextLayoutBuilder setMaxWidth(@Px int maxWidth) {
    mMaxWidth = maxWidth;
    mMaxWidthMode = PIXELS;
    return this;
  }

  /** @return The density of this layout. If unset, defaults to 1.0 */
  public float getDensity() {
    return mParams.paint.density;
  }

  /**
   * Sets the density of this layout. This should typically be set to your current display's density
   *
   * @param density The density desired
   * @return This {@link TextLayoutBuilder}
   */
  public TextLayoutBuilder setDensity(float density) {
    if (mParams.paint.density != density) {
      mParams.createNewPaintIfNeeded();
      mParams.paint.density = density;
      mSavedLayout = null;
    }
    return this;
  }

  /** @return The justification mode of this layout. */
  @RequiresApi(api = Build.VERSION_CODES.O)
  public int getJustificationMode() {
    return mParams.justificationMode;
  }

  /**
   * Set justification mode. The default value is JUSTIFICATION_MODE_NONE. If the last line is too
   * short for justification, the last line will be displayed with the alignment
   *
   * @param justificationMode The justification mode to use
   * @return This {@link TextLayoutBuilder} instance
   */
  @RequiresApi(api = Build.VERSION_CODES.O)
  public TextLayoutBuilder setJustificationMode(int justificationMode) {
    if (mParams.justificationMode != justificationMode) {
      mParams.justificationMode = justificationMode;
      if (Build.VERSION.SDK_INT >= 26) {
        mSavedLayout = null;
      }
    }
    return this;
  }

  /**
   * Sets whether zero-length text should be laid out or not.
   *
   * @param shouldLayoutZeroLengthText True to lay out zero-length text, false otherwise.
   * @return This {@link TextLayoutBuilder} instance
   */
  public TextLayoutBuilder setShouldLayoutZeroLengthText(boolean shouldLayoutZeroLengthText) {
    if (mParams.shouldLayoutZeroLengthText != shouldLayoutZeroLengthText) {
      mParams.shouldLayoutZeroLengthText = shouldLayoutZeroLengthText;
      if (mParams.text.length() == 0) {
        mSavedLayout = null;
      }
    }

    return this;
  }

  /**
   * Builds and returns a {@link Layout}.
   *
   * @return A {@link Layout} based on the parameters set. Returns null if no text was specified and
   *     empty text is not explicitly allowed (see {@link #setShouldLayoutZeroLengthText(boolean)}).
   */
  @Nullable
  public Layout build() {
    // Return the cached layout if no property changed.
    if (mShouldCacheLayout && mSavedLayout != null) {
      return mSavedLayout;
    }

    if (mParams.text == null
        || (mParams.text.length() == 0 && !mParams.shouldLayoutZeroLengthText)) {
      return null;
    }

    boolean hasClickableSpans = false;
    int hashCode = -1;

    if (mShouldCacheLayout && mParams.text instanceof Spannable) {
      ClickableSpan[] spans =
          ((Spannable) mParams.text).getSpans(0, mParams.text.length() - 1, ClickableSpan.class);
      hasClickableSpans = spans.length > 0;
    }

    // If the text has ClickableSpans, it will be bound to different
    // click listeners each time. It is unsafe to cache these text Layouts.
    // Hence they will not be in cache.
    if (mShouldCacheLayout && !hasClickableSpans) {
      hashCode = mParams.hashCode();
      Layout cachedLayout = sCache.get(hashCode);
      if (cachedLayout != null) {
        return cachedLayout;
      }
    }

    BoringLayout.Metrics metrics = null;

    int numLines = mParams.singleLine ? 1 : mParams.maxLines;

    // Try creating a boring layout only if singleLine is requested.
    if (numLines == 1) {
      try {
        metrics = BoringLayout.isBoring(mParams.text, mParams.paint);
      } catch (NullPointerException e) {
        // On older Samsung devices (< M), we sometimes run into a NPE here where a FontMetricsInt
        // object created within BoringLayout is not properly null-checked within TextLine.
        // Its ok to ignore this exception since we'll create a regular StaticLayout later.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          // If we see this on M or above, then its something else.
          throw e;
        }
      }
    }

    // getDesiredWidth here is used to ensure we layout text at the same size which it is measured.
    // If we used a large static value it would break RTL due to drawing text at the very end of the
    // large value.
    int width;
    switch (mParams.measureMode) {
      case MEASURE_MODE_UNSPECIFIED:
        width = (int) Math.ceil(Layout.getDesiredWidth(mParams.text, mParams.paint));
        break;
      case MEASURE_MODE_EXACTLY:
        width = mParams.width;
        break;
      case MEASURE_MODE_AT_MOST:
        width =
            Math.min(
                (int) Math.ceil(Layout.getDesiredWidth(mParams.text, mParams.paint)),
                mParams.width);
        break;
      default:
        throw new IllegalStateException("Unexpected measure mode " + mParams.measureMode);
    }

    final int lineHeight = mParams.getLineHeight();
    if (mMaxWidthMode == EMS) {
      width = Math.min(width, mMaxWidth * lineHeight);
    } else {
      width = Math.min(width, mMaxWidth);
    }

    if (mMinWidthMode == EMS) {
      width = Math.max(width, mMinWidth * lineHeight);
    } else {
      width = Math.max(width, mMinWidth);
    }

    Layout layout;
    if (metrics != null) {
      layout =
          BoringLayout.make(
              mParams.text,
              mParams.paint,
              width,
              mParams.alignment,
              mParams.spacingMult,
              mParams.spacingAdd,
              metrics,
              mParams.includePadding,
              mParams.ellipsize,
              width);
    } else {
      while (true) {
        try {
          layout =
              StaticLayoutHelper.make(
                  mParams.text,
                  0,
                  mParams.text.length(),
                  mParams.paint,
                  width,
                  mParams.alignment,
                  mParams.spacingMult,
                  mParams.spacingAdd,
                  mParams.includePadding,
                  mParams.ellipsize,
                  width,
                  numLines,
                  mParams.textDirection,
                  mParams.breakStrategy,
                  mParams.hyphenationFrequency,
                  mParams.justificationMode,
                  mParams.leftIndents,
                  mParams.rightIndents,
                  mParams.useLineSpacingFromFallbacks);
        } catch (IndexOutOfBoundsException e) {
          // Workaround for https://code.google.com/p/android/issues/detail?id=35412
          if (!(mParams.text instanceof String)) {
            // remove all Spannables and re-try
            Log.e("TextLayoutBuilder", "Hit bug #35412, retrying with Spannables removed", e);
            mParams.text = mParams.text.toString();
            continue;
          } else {
            // If it still happens with all Spannables removed we'll bubble the exception up
            throw e;
          }
        }

        break;
      }
    }

    // Do not cache if the text has ClickableSpans.
    if (mShouldCacheLayout && !hasClickableSpans) {
      mSavedLayout = layout;
      sCache.put(hashCode, layout);
    }

    // Force a new paint.
    mParams.mForceNewPaint = true;

    if (mShouldWarmText && mGlyphWarmer != null) {
      // Draw the text in a background thread to warm the cache.
      mGlyphWarmer.warmLayout(layout);
    }

    return layout;
  }
}
