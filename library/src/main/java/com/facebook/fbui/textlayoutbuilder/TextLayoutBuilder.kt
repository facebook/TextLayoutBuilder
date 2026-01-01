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

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.text.BoringLayout
import android.text.Layout
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextUtils.TruncateAt
import android.text.style.ClickableSpan
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import androidx.annotation.Px
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import androidx.collection.LruCache
import androidx.core.text.TextDirectionHeuristicCompat
import androidx.core.text.TextDirectionHeuristicsCompat
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/**
 * An utility class to create text [Layout]s easily.
 *
 * This class uses a Builder pattern to allow re-using the same object to create text [Layout]s with
 * similar properties.
 */
class TextLayoutBuilder {

  /**
   * Measure mode constants similar to [android.view.View.MeasureSpec]
   *
   * @see .MEASURE_MODE_UNSPECIFIED
   * @see .MEASURE_MODE_EXACTLY
   * @see .MEASURE_MODE_AT_MOST
   */
  @Retention(AnnotationRetention.SOURCE)
  @IntDef(MEASURE_MODE_UNSPECIFIED, MEASURE_MODE_EXACTLY, MEASURE_MODE_AT_MOST)
  annotation class MeasureMode

  private var _minWidth = 0
  private var minWidthMode = PIXELS
  private var _maxWidth = Int.MAX_VALUE
  private var maxWidthMode = PIXELS

  /** Params for creating the layout. */
  @VisibleForTesting
  inner class Params {
    var paint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    var mShadowDx: Float = 0f

    var mShadowDy: Float = 0f

    var mShadowRadius: Float = 0f

    var mShadowColor: Int = 0

    var width: Int = 0

    @MeasureMode var measureMode: Int = 0

    var text: CharSequence? = null

    var color: ColorStateList? = null

    var spacingMult: Float = DEFAULT_SPACING_MULT

    var spacingAdd: Float = DEFAULT_SPACING_ADD

    var lineHeight: Float = DEFAULT_LINE_HEIGHT

    var includePadding: Boolean = true

    var useLineSpacingFromFallbacks: Boolean = Build.VERSION.SDK_INT >= 28

    var shouldLayoutZeroLengthText: Boolean = false

    var ellipsize: TruncateAt? = null

    var singleLine: Boolean = false

    var maxLines: Int = DEFAULT_MAX_LINES

    var alignment: Layout.Alignment? = Layout.Alignment.ALIGN_NORMAL

    var textDirection: TextDirectionHeuristicCompat? = TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR

    var breakStrategy: Int = 0

    var hyphenationFrequency: Int = 0

    var justificationMode: Int = 0 // JUSTIFICATION_MODE_NONE

    var leftIndents: IntArray? = null

    var rightIndents: IntArray? = null

    var mForceNewPaint: Boolean = false

    /** Create a new paint after the builder builds for the first time. */
    fun createNewPaintIfNeeded() {
      // Once after build() is called, it is not safe to set properties
      // on the paint as we cache the text layouts.
      // Hence we create a new paint object,
      // if we ever change one of the paint's properties.
      if (mForceNewPaint) {
        val newPaint = TextPaint(paint)
        newPaint.set(paint)
        this.paint = newPaint
        mForceNewPaint = false
      }
    }

    fun getLineHeight(): Int = Math.round(paint.getFontMetricsInt(null) * spacingMult + spacingAdd)

    override fun hashCode(): Int {
      var hashCode = 1

      // Hashing the TextPaint object
      hashCode = 31 * hashCode + paint.color
      hashCode = 31 * hashCode + java.lang.Float.floatToIntBits(paint.textSize)
      hashCode = 31 * hashCode + (if (paint.typeface != null) paint.typeface.hashCode() else 0)
      hashCode = 31 * hashCode + java.lang.Float.floatToIntBits(mShadowDx)
      hashCode = 31 * hashCode + java.lang.Float.floatToIntBits(mShadowDy)
      hashCode = 31 * hashCode + java.lang.Float.floatToIntBits(mShadowRadius)
      hashCode = 31 * hashCode + mShadowColor
      hashCode = 31 * hashCode + paint.linkColor
      hashCode = 31 * hashCode + java.lang.Float.floatToIntBits(paint.density)
      hashCode = 31 * hashCode + paint.drawableState.contentHashCode()

      hashCode = 31 * hashCode + width
      hashCode = 31 * hashCode + measureMode
      hashCode = 31 * hashCode + java.lang.Float.floatToIntBits(spacingMult)
      hashCode = 31 * hashCode + java.lang.Float.floatToIntBits(spacingAdd)
      hashCode = 31 * hashCode + java.lang.Float.floatToIntBits(lineHeight)
      hashCode = 31 * hashCode + (if (includePadding) 1 else 0)
      hashCode = 31 * hashCode + (if (useLineSpacingFromFallbacks) 1 else 0)
      hashCode = 31 * hashCode + (if (ellipsize != null) ellipsize.hashCode() else 0)
      hashCode = 31 * hashCode + (if (singleLine) 1 else 0)
      hashCode = 31 * hashCode + maxLines
      hashCode = 31 * hashCode + (if (alignment != null) alignment.hashCode() else 0)
      hashCode = 31 * hashCode + (if (textDirection != null) textDirection.hashCode() else 0)
      hashCode = 31 * hashCode + breakStrategy
      hashCode = 31 * hashCode + hyphenationFrequency
      hashCode = 31 * hashCode + leftIndents.contentHashCode()
      hashCode = 31 * hashCode + rightIndents.contentHashCode()
      hashCode = 31 * hashCode + (if (text != null) text.hashCode() else 0)

      return hashCode
    }
  }

  // Params for the builder.
  @JvmField @VisibleForTesting val mParams: Params = Params()

  // Locally cached layout for an instance.
  private var savedLayout: Layout? = null

  /**
   * Returns the GlyphWarmer used by the TextLayoutBuilder.
   *
   * @return The GlyphWarmer for this TextLayoutBuilder
   */
  // Text layout glyph warmer.
  var glyphWarmer: GlyphWarmer? = null
    private set

  /**
   * Returns whether the TextLayoutBuilder should cache the layout.
   *
   * @return Whether the TextLayoutBuilder should cache the layout
   */
  // Cache layout or not.
  var shouldCacheLayout: Boolean = true
    private set

  /**
   * Returns whether the TextLayoutBuilder should warm the layout.
   *
   * @return Whether the TextLayoutBuilder should warm the layout
   */
  // Warm layout or not.
  var shouldWarmText: Boolean = false
    private set

  /**
   * Sets the intended width of the text layout.
   *
   * @param width The width of the text layout
   * @return This [TextLayoutBuilder] instance
   * @see .setWidth
   */
  fun setWidth(@Px width: Int): TextLayoutBuilder =
      this.setWidth(width, if (width <= 0) MEASURE_MODE_UNSPECIFIED else MEASURE_MODE_EXACTLY)

  /**
   * Sets the intended width of the text layout while respecting the measure mode.
   *
   * @param width The width of the text layout
   * @param measureMode The mode with which to treat the given width
   * @return This [TextLayoutBuilder] instance
   * @see .setWidth
   */
  fun setWidth(@Px width: Int, @MeasureMode measureMode: Int): TextLayoutBuilder {
    if (mParams.width != width || mParams.measureMode != measureMode) {
      mParams.width = width
      mParams.measureMode = measureMode
      savedLayout = null
    }
    return this
  }

  val text: CharSequence?
    get() = mParams.text

  /**
   * Sets the text for the layout.
   *
   * @param text The text for the layout
   * @return This [TextLayoutBuilder] instance
   */
  fun setText(text: CharSequence?): TextLayoutBuilder {
    if (text === mParams.text) {
      return this
    }

    if (text is SpannableStringBuilder) {
      // Workaround for https://issuetracker.google.com/issues/117666255
      // We cannot use getSpans here because it omits null spans, even though they exist
      // So instead we just execute the bug repro and catch the NPE
      try {
        text.hashCode()
      } catch (e: NullPointerException) {
        throw IllegalArgumentException(
            "The given text contains a null span. Due to an Android framework bug, this will cause an exception later down the line.",
            e,
        )
      }
    }

    if (text != null && text == mParams.text) {
      return this
    }

    mParams.text = text
    savedLayout = null
    return this
  }

  val textSize: Float
    get() = mParams.paint.textSize

  /**
   * Sets the text size for the layout.
   *
   * @param size The text size in pixels
   * @return This [TextLayoutBuilder] instance
   */
  fun setTextSize(size: Int): TextLayoutBuilder {
    if (mParams.paint.textSize != size.toFloat()) {
      mParams.createNewPaintIfNeeded()
      mParams.paint.textSize = size.toFloat()
      savedLayout = null
    }
    return this
  }

  @get:ColorInt
  val textColor: Int
    get() = mParams.paint.color

  /**
   * Sets the text color for the layout.
   *
   * @param color The text color for the layout
   * @return This [TextLayoutBuilder] instance
   */
  fun setTextColor(@ColorInt color: Int): TextLayoutBuilder {
    mParams.createNewPaintIfNeeded()
    mParams.color = null
    mParams.paint.color = color
    savedLayout = null
    return this
  }

  /**
   * Sets the text color for the layout.
   *
   * @param colorStateList The text color state list for the layout
   * @return This [TextLayoutBuilder] instance
   */
  fun setTextColor(colorStateList: ColorStateList?): TextLayoutBuilder {
    mParams.createNewPaintIfNeeded()
    mParams.color = colorStateList
    mParams.paint.color = if (mParams.color != null) mParams.color!!.defaultColor else Color.BLACK
    savedLayout = null
    return this
  }

  @get:ColorInt
  val linkColor: Int
    get() = mParams.paint.linkColor

  /**
   * Sets the link color for the text in the layout.
   *
   * @param linkColor The link color
   * @return This [TextLayoutBuilder] instance
   */
  fun setLinkColor(@ColorInt linkColor: Int): TextLayoutBuilder {
    if (mParams.paint.linkColor != linkColor) {
      mParams.createNewPaintIfNeeded()
      mParams.paint.linkColor = linkColor
      savedLayout = null
    }
    return this
  }

  val textSpacingExtra: Float
    get() = mParams.spacingAdd

  /**
   * Sets the text extra spacing for the layout. Extra spacing will be ignored if
   * [TextLayoutBuilder#setLineHeight(float)] was used to set an explicit line height.
   *
   * @param spacingExtra the extra space that is added to the height of each line
   * @return This [TextLayoutBuilder] instance
   */
  fun setTextSpacingExtra(spacingExtra: Float): TextLayoutBuilder {
    if (mParams.lineHeight == DEFAULT_LINE_HEIGHT && mParams.spacingAdd != spacingExtra) {
      mParams.spacingAdd = spacingExtra
      savedLayout = null
    }
    return this
  }

  val textSpacingMultiplier: Float
    get() = mParams.spacingMult

  /**
   * Sets the line spacing multiplier for the layout. Line spacing multiplier will be ignored if
   * [TextLayoutBuilder#setLineHeight(float)] was used to set an explicit line height.
   *
   * @param spacingMultiplier the value by which each line's height is multiplied
   * @return This [TextLayoutBuilder] instance
   */
  fun setTextSpacingMultiplier(spacingMultiplier: Float): TextLayoutBuilder {
    if (mParams.lineHeight == DEFAULT_LINE_HEIGHT && mParams.spacingMult != spacingMultiplier) {
      mParams.spacingMult = spacingMultiplier
      savedLayout = null
    }
    return this
  }

  val lineHeight: Float
    get() = mParams.getLineHeight().toFloat()

  /**
   * Sets the line height for this layout.
   *
   * This mimics the behavior of
   * https://developer.android.com/reference/android/widget/TextView.html#setLineHeight(int) and
   * should not be used with [TextLayoutBuilder#setTextSpacingExtra(float)] or
   * [TextLayoutBuilder#setTextSpacingMultiplier(float)].
   *
   * @param lineHeight the line height between two lines of text in px.
   * @return This [TextLayoutBuilder] instance.
   */
  fun setLineHeight(lineHeight: Float): TextLayoutBuilder {
    if (mParams.lineHeight != lineHeight) {
      mParams.lineHeight = lineHeight
      mParams.spacingAdd = lineHeight - mParams.paint.getFontMetrics(null)
      mParams.spacingMult = 1.0f
      savedLayout = null
    }
    return this
  }

  @get:RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  val letterSpacing: Float
    get() = mParams.paint.letterSpacing

  /**
   * Sets text letter-spacing in em units. Typical values for slight expansion will be around 0.05.
   * Negative values tighten text.
   *
   * @param letterSpacing A text letter-space value in ems.
   * @see .getLetterSpacing
   */
  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  fun setLetterSpacing(letterSpacing: Float): TextLayoutBuilder {
    if (this.letterSpacing != letterSpacing) {
      mParams.createNewPaintIfNeeded()
      mParams.paint.letterSpacing = letterSpacing
      savedLayout = null
    }
    return this
  }

  val includeFontPadding: Boolean
    get() = mParams.includePadding

  /**
   * Set whether the text Layout includes extra top and bottom padding to make room for accents that
   * go above the normal ascent and descent.
   *
   * The default is true.
   *
   * @param shouldInclude Whether to include font padding or not
   * @return This [TextLayoutBuilder] instance
   */
  fun setIncludeFontPadding(shouldInclude: Boolean): TextLayoutBuilder {
    if (mParams.includePadding != shouldInclude) {
      mParams.includePadding = shouldInclude
      savedLayout = null
    }
    return this
  }

  val alignment: Layout.Alignment?
    get() = mParams.alignment

  /**
   * Sets text alignment for the layout.
   *
   * @param alignment The text alignment for the layout
   * @return This [TextLayoutBuilder] instance
   */
  fun setAlignment(alignment: Layout.Alignment): TextLayoutBuilder {
    if (mParams.alignment != alignment) {
      mParams.alignment = alignment
      savedLayout = null
    }
    return this
  }

  val textDirection: TextDirectionHeuristicCompat?
    get() = mParams.textDirection

  /**
   * Sets the text direction heuristic for the layout.
   *
   * TextDirectionHeuristicCompat describes how to evaluate the text of this Layout to know whether
   * to use RTL or LTR text direction
   *
   * @param textDirection The text direction heuristic for the layout
   * @return This [TextLayoutBuilder] instance
   */
  fun setTextDirection(textDirection: TextDirectionHeuristicCompat): TextLayoutBuilder {
    if (mParams.textDirection !== textDirection) {
      mParams.textDirection = textDirection
      savedLayout = null
    }
    return this
  }

  /**
   * Sets the shadow layer for the layout.
   *
   * @param radius The radius of the blur for shadow
   * @param dx The horizontal translation of the origin
   * @param dy The vertical translation of the origin
   * @param color The shadow color
   * @return This [TextLayoutBuilder] instance
   */
  fun setShadowLayer(radius: Float, dx: Float, dy: Float, @ColorInt color: Int): TextLayoutBuilder {
    mParams.createNewPaintIfNeeded()
    mParams.mShadowRadius = radius
    mParams.mShadowDx = dx
    mParams.mShadowDy = dy
    mParams.mShadowColor = color
    mParams.paint.setShadowLayer(radius, dx, dy, color)
    savedLayout = null
    return this
  }

  /**
   * Sets a text style for the layout.
   *
   * @param style The text style for the layout
   * @return This [TextLayoutBuilder] instance
   */
  fun setTextStyle(style: Int): TextLayoutBuilder = setTypeface(Typeface.defaultFromStyle(style))

  val typeface: Typeface
    get() = mParams.paint.typeface

  /**
   * Sets the typeface used by this TextLayoutBuilder.
   *
   * @param typeface The typeface for this TextLayoutBuilder
   * @return This [TextLayoutBuilder] instance
   */
  fun setTypeface(typeface: Typeface?): TextLayoutBuilder {
    if (mParams.paint.typeface !== typeface) {
      mParams.createNewPaintIfNeeded()
      mParams.paint.setTypeface(typeface)
      savedLayout = null
    }
    return this
  }

  val drawableState: IntArray
    get() = mParams.paint.drawableState

  /**
   * Updates the text colors based on the drawable state.
   *
   * @param drawableState The current drawable state of the View holding this layout
   * @return This [TextLayoutBuilder] instance
   */
  fun setDrawableState(drawableState: IntArray?): TextLayoutBuilder {
    mParams.createNewPaintIfNeeded()
    mParams.paint.drawableState = drawableState

    if (mParams.color?.isStateful == true) {
      val color = mParams.color!!.getColorForState(drawableState, 0)
      mParams.paint.color = color
      savedLayout = null
    }
    return this
  }

  val ellipsize: TruncateAt?
    get() = mParams.ellipsize

  /**
   * Sets the ellipsis location for the layout.
   *
   * @param ellipsize The ellipsis location in the layout
   * @return This [TextLayoutBuilder] instance
   */
  fun setEllipsize(ellipsize: TruncateAt?): TextLayoutBuilder {
    if (mParams.ellipsize != ellipsize) {
      mParams.ellipsize = ellipsize
      savedLayout = null
    }
    return this
  }

  /**
   * Set whether to respect the ascent and descent of the fallback fonts that are used in displaying
   * the text (which is needed to avoid text from consecutive lines running into each other).
   *
   * If set, fallback fonts that end up getting used can increase the ascent and descent of the
   * lines that they are used on.
   *
   * This behavior is defaulted to true on API >= 28 and false otherwise.
   */
  @RequiresApi(api = 28)
  fun setUseLineSpacingFromFallbacks(status: Boolean): TextLayoutBuilder {
    if (mParams.useLineSpacingFromFallbacks != status) {
      mParams.useLineSpacingFromFallbacks = status
      savedLayout = null
    }
    return this
  }

  val useLineSpacingFromFallbacks: Boolean
    /**
     * Returns whether to use line spacing from fallback fonts or not. See
     * [setUseLineSpacingFromFallbacks]
     */
    get() = mParams.useLineSpacingFromFallbacks

  val singleLine: Boolean
    get() = mParams.singleLine

  /**
   * Sets whether the text should be in a single line or not.
   *
   * @param singleLine Whether the text should be in a single line or not
   * @return This [TextLayoutBuilder] instance
   * @see .setMaxLines
   */
  fun setSingleLine(singleLine: Boolean): TextLayoutBuilder {
    if (mParams.singleLine != singleLine) {
      mParams.singleLine = singleLine
      savedLayout = null
    }
    return this
  }

  val maxLines: Int
    get() = mParams.maxLines

  /**
   * Sets a maximum number of lines to be shown by the Layout.
   *
   * Note: Gingerbread always default to two lines max when ellipsized. This cannot be changed. Use
   * a TextView if you want more control over the number of lines.
   *
   * @param maxLines The number of maxLines to show in this Layout
   * @return This [TextLayoutBuilder] instance
   * @see .setSingleLine
   */
  fun setMaxLines(maxLines: Int): TextLayoutBuilder {
    if (mParams.maxLines != maxLines) {
      mParams.maxLines = maxLines
      savedLayout = null
    }
    return this
  }

  val breakStrategy: Int
    get() = mParams.breakStrategy

  /**
   * Sets a break strategy breaking paragraphs into lines.
   *
   * @param breakStrategy The break strategy for breaking paragraphs into lines
   * @return This [TextLayoutBuilder] instance
   */
  fun setBreakStrategy(breakStrategy: Int): TextLayoutBuilder {
    if (mParams.breakStrategy != breakStrategy) {
      mParams.breakStrategy = breakStrategy
      savedLayout = null
    }
    return this
  }

  val hyphenationFrequency: Int
    get() = mParams.hyphenationFrequency

  /**
   * Sets the frequency of automatic hyphenation to use when determining word breaks.
   *
   * @param hyphenationFrequency The frequency of automatic hyphenation to use for word breaks
   * @return This [TextLayoutBuilder] instance
   */
  fun setHyphenationFrequency(hyphenationFrequency: Int): TextLayoutBuilder {
    if (mParams.hyphenationFrequency != hyphenationFrequency) {
      mParams.hyphenationFrequency = hyphenationFrequency
      if (Build.VERSION.SDK_INT >= 23) {
        savedLayout = null
      }
    }
    return this
  }

  val leftIndents: IntArray?
    get() = mParams.leftIndents

  val rightIndents: IntArray?
    get() = mParams.rightIndents

  /**
   * Sets the left and right indents for this TextLayoutBuilder.
   *
   * The arrays hold an indent amount, one per line, measured in pixels. For lines past the last
   * element in the array, the last element repeats.
   *
   * @param leftIndents The left indents for the paragraph
   * @param rightIndents The left indents for the paragraph
   * @return This [TextLayoutBuilder] instance
   */
  fun setIndents(leftIndents: IntArray?, rightIndents: IntArray?): TextLayoutBuilder {
    mParams.leftIndents = leftIndents
    mParams.rightIndents = rightIndents
    savedLayout = null
    return this
  }

  /**
   * Sets whether the text layout should be cached or not.
   *
   * Note: If the Layout contains [ClickableSpan]s, the layout will not be cached.
   *
   * @param shouldCacheLayout True to cache the text layout, false otherwise
   * @return This [TextLayoutBuilder] instance
   */
  fun setShouldCacheLayout(shouldCacheLayout: Boolean): TextLayoutBuilder {
    this.shouldCacheLayout = shouldCacheLayout
    return this
  }

  /**
   * Sets whether the text should be warmed or not.
   *
   * Note: Setting this true is highly effective for large blurbs of text. This method has to be
   * called before the draw pass.
   *
   * @param shouldWarmText True to warm the text layout, false otherwise
   * @return This [TextLayoutBuilder] instance
   * @see .setGlyphWarmer
   */
  fun setShouldWarmText(shouldWarmText: Boolean): TextLayoutBuilder {
    this.shouldWarmText = shouldWarmText
    return this
  }

  /**
   * Sets the glyph warmer to use.
   *
   * @param glyphWarmer GlyphWarmer to use to warm the text layout
   * @return This [TextLayoutBuilder] instance
   * @see .setShouldWarmText
   */
  fun setGlyphWarmer(glyphWarmer: GlyphWarmer?): TextLayoutBuilder {
    this.glyphWarmer = glyphWarmer
    return this
  }

  val minEms: Int
    /**
     * Returns the min width expressed in ems.
     *
     * @return the min width expressed in ems or -1
     * @see .setMinEms
     */
    get() = if (minWidthMode == EMS) _minWidth else -1

  /**
   * Sets the min width expressed in ems.
   *
   * @param minEms min width expressed in ems
   * @return This [TextLayoutBuilder] instance
   * @see .setMaxEms
   * @see .setMinWidth
   */
  fun setMinEms(minEms: Int): TextLayoutBuilder {
    _minWidth = minEms
    minWidthMode = EMS
    return this
  }

  @get:Px
  val minWidth: Int
    /**
     * Returns the min width expressed in pixels.
     *
     * @return the min width expressed in pixels or -1, if the min width was set in ems instead
     * @see .setMinWidth
     */
    get() = if (minWidthMode == PIXELS) _minWidth else -1

  /**
   * Sets the min width expressed in pixels.
   *
   * @param minWidth min width expressed in pixels.
   * @return This [TextLayoutBuilder] instance
   * @see .setMaxWidth
   * @see .setMinEms
   */
  fun setMinWidth(@Px minWidth: Int): TextLayoutBuilder {
    this._minWidth = minWidth
    minWidthMode = PIXELS
    return this
  }

  val maxEms: Int
    /**
     * Returns the max width expressed in ems.
     *
     * @return the max width expressed in ems or -1, if max width is set in pixels instead
     * @see .setMaxEms
     */
    get() = if (maxWidthMode == EMS) _maxWidth else -1

  /**
   * Sets the max width expressed in ems.
   *
   * @param maxEms max width expressed in ems
   * @return This [TextLayoutBuilder] instance
   * @see .setMaxWidth
   * @see .setMinEms
   */
  fun setMaxEms(maxEms: Int): TextLayoutBuilder {
    _maxWidth = maxEms
    maxWidthMode = EMS
    return this
  }

  @get:Px
  val maxWidth: Int
    /**
     * Returns the max width expressed in pixels.
     *
     * @return the max width expressed in pixels or -1, if the max width was set in ems instead
     * @see .setMaxWidth
     */
    get() = if (maxWidthMode == PIXELS) _maxWidth else -1

  /**
   * Sets the max width expressed in pixels.
   *
   * @param maxWidth max width expressed in pixels
   * @return This [TextLayoutBuilder] instance
   * @see .setMaxEms
   * @see .setMinWidth
   */
  fun setMaxWidth(@Px maxWidth: Int): TextLayoutBuilder {
    this._maxWidth = maxWidth
    maxWidthMode = PIXELS
    return this
  }

  val density: Float
    /** @return The density of this layout. If unset, defaults to 1.0 */
    get() = mParams.paint.density

  /**
   * Sets the density of this layout. This should typically be set to your current display's density
   *
   * @param density The density desired
   * @return This [TextLayoutBuilder]
   */
  fun setDensity(density: Float): TextLayoutBuilder {
    if (mParams.paint.density != density) {
      mParams.createNewPaintIfNeeded()
      mParams.paint.density = density
      savedLayout = null
    }
    return this
  }

  @get:RequiresApi(api = Build.VERSION_CODES.O)
  val justificationMode: Int
    get() = mParams.justificationMode

  /**
   * Set justification mode. The default value is JUSTIFICATION_MODE_NONE. If the last line is too
   * short for justification, the last line will be displayed with the alignment
   *
   * @param justificationMode The justification mode to use
   * @return This [TextLayoutBuilder] instance
   */
  @RequiresApi(api = Build.VERSION_CODES.O)
  fun setJustificationMode(justificationMode: Int): TextLayoutBuilder {
    if (mParams.justificationMode != justificationMode) {
      mParams.justificationMode = justificationMode
      if (Build.VERSION.SDK_INT >= 26) {
        savedLayout = null
      }
    }
    return this
  }

  /**
   * Sets whether zero-length text should be laid out or not.
   *
   * @param shouldLayoutZeroLengthText True to lay out zero-length text, false otherwise.
   * @return This [TextLayoutBuilder] instance
   */
  fun setShouldLayoutZeroLengthText(shouldLayoutZeroLengthText: Boolean): TextLayoutBuilder {
    if (mParams.shouldLayoutZeroLengthText != shouldLayoutZeroLengthText) {
      mParams.shouldLayoutZeroLengthText = shouldLayoutZeroLengthText
      if (mParams.text!!.length == 0) {
        savedLayout = null
      }
    }

    return this
  }

  /**
   * Builds and returns a [Layout].
   *
   * @return A [Layout] based on the parameters set. Returns null if no text was specified and empty
   *   text is not explicitly allowed (see [setShouldLayoutZeroLengthText(boolean)]).
   */
  fun build(): Layout? {
    // Return the cached layout if no property changed.
    if (shouldCacheLayout && savedLayout != null) {
      return savedLayout
    }

    if (
        mParams.text == null || (mParams.text!!.length == 0 && !mParams.shouldLayoutZeroLengthText)
    ) {
      return null
    }

    var hasClickableSpans = false
    var hashCode = -1

    if (shouldCacheLayout && mParams.text is Spannable) {
      val spans =
          (mParams.text as Spannable).getSpans(
              0,
              mParams.text!!.length - 1,
              ClickableSpan::class.java,
          )
      hasClickableSpans = spans.size > 0
    }

    // If the text has ClickableSpans, it will be bound to different
    // click listeners each time. It is unsafe to cache these text Layouts.
    // Hence they will not be in cache.
    if (shouldCacheLayout && !hasClickableSpans) {
      hashCode = mParams.hashCode()
      val cachedLayout = sCache[hashCode]
      if (cachedLayout != null) {
        return cachedLayout
      }
    }

    var metrics: BoringLayout.Metrics? = null

    val numLines = if (mParams.singleLine) 1 else mParams.maxLines

    // Try creating a boring layout only if singleLine is requested.
    if (numLines == 1) {
      metrics = isBoringLayout
    }

    // getDesiredWidth here is used to ensure we layout text at the same size which it is measured.
    // If we used a large static value it would break RTL due to drawing text at the very end of the
    // large value.
    var width =
        when (mParams.measureMode) {
          MEASURE_MODE_UNSPECIFIED ->
              ceil(Layout.getDesiredWidth(mParams.text, mParams.paint).toDouble()).toInt()

          MEASURE_MODE_EXACTLY -> mParams.width
          MEASURE_MODE_AT_MOST ->
              min(
                  ceil(Layout.getDesiredWidth(mParams.text, mParams.paint).toDouble()).toInt(),
                  mParams.width,
              )

          else -> throw IllegalStateException("Unexpected measure mode ${mParams.measureMode}")
        }

    val lineHeight = mParams.getLineHeight()
    width =
        if (maxWidthMode == EMS) {
          min(width, _maxWidth * lineHeight)
        } else {
          min(width, _maxWidth)
        }

    width =
        if (minWidthMode == EMS) {
          max(width, _minWidth * lineHeight)
        } else {
          max(width, _minWidth)
        }

    var layout: Layout
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
              width,
          )
    } else {
      while (true) {
        try {
          layout =
              StaticLayoutHelper.make(
                  mParams.text!!,
                  0,
                  mParams.text!!.length,
                  mParams.paint,
                  width,
                  mParams.alignment!!,
                  mParams.spacingMult,
                  mParams.spacingAdd,
                  mParams.includePadding,
                  mParams.ellipsize,
                  width,
                  numLines,
                  mParams.textDirection!!,
                  mParams.breakStrategy,
                  mParams.hyphenationFrequency,
                  mParams.justificationMode,
                  mParams.leftIndents,
                  mParams.rightIndents,
                  mParams.useLineSpacingFromFallbacks,
              )
        } catch (e: IndexOutOfBoundsException) {
          // Workaround for https://code.google.com/p/android/issues/detail?id=35412
          if (mParams.text !is String) {
            // remove all Spannables and re-try
            Log.e("TextLayoutBuilder", "Hit bug #35412, retrying with Spannables removed", e)
            mParams.text = mParams.text.toString()
            continue
          } else {
            // If it still happens with all Spannables removed we'll bubble the exception up
            throw e
          }
        }

        break
      }
    }

    // Do not cache if the text has ClickableSpans.
    if (shouldCacheLayout && !hasClickableSpans) {
      savedLayout = layout
      sCache.put(hashCode, layout)
    }

    // Force a new paint.
    mParams.mForceNewPaint = true

    if (shouldWarmText && glyphWarmer != null) {
      // Draw the text in a background thread to warm the cache.
      glyphWarmer!!.warmLayout(layout)
    }

    return layout
  }

  private val isBoringLayout: BoringLayout.Metrics?
    get() {
      try {
        return BoringLayout.isBoring(mParams.text, mParams.paint)
      } catch (e: NullPointerException) {
        // On older Samsung devices (< M), we sometimes run into a NPE here where a FontMetricsInt
        // object created within BoringLayout is not properly null-checked within TextLine.
        // Its ok to ignore this exception since we'll create a regular StaticLayout later.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          // If we see this on M or above, then its something else.
          throw e
        }
        return null
      }
    }

  companion object {
    const val MEASURE_MODE_UNSPECIFIED: Int = 0
    const val MEASURE_MODE_EXACTLY: Int = 1
    const val MEASURE_MODE_AT_MOST: Int = 2

    // Default maxLines.
    const val DEFAULT_MAX_LINES: Int = Int.MAX_VALUE

    private const val DEFAULT_SPACING_ADD = 0.0f
    private const val DEFAULT_SPACING_MULT = 1.0f
    private const val DEFAULT_LINE_HEIGHT = Float.MAX_VALUE

    private const val EMS = 1
    private const val PIXELS = 2

    private const val IS_BORING_CALL_TIMEOUT = 500L

    // Cache for text layouts.
    @JvmField @VisibleForTesting val sCache: LruCache<Int, Layout> = LruCache(100)
  }
}
