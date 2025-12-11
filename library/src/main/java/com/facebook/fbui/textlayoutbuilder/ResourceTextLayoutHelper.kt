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

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.TextUtils.TruncateAt
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes

/** An utility class to update a [TextLayoutBuilder] from an Android resource. */
object ResourceTextLayoutHelper {

  // Font size in pixels.
  private const val DEFAULT_TEXT_SIZE_PX = 15

  /**
   * Sets the values for a TextLayoutBuilder from a style resource.
   *
   * @param builder The TextLayoutBuilder
   * @param context The Context to use for resolving the attributes
   * @param styleRes The style resource identifier
   */
  @JvmStatic
  fun updateFromStyleResource(
      builder: TextLayoutBuilder,
      context: Context,
      @StyleRes styleRes: Int,
  ) {
    updateFromStyleResource(builder, context, 0, styleRes)
  }

  /**
   * Sets the values for a TextLayoutBuilder from a style resource or a themed attribute.
   *
   * @param builder The TextLayoutBuilder
   * @param context The Context to use for resolving the attributes
   * @param styleAttr The themed style attribute
   * @param styleRes The style resource identifier
   */
  @JvmStatic
  fun updateFromStyleResource(
      builder: TextLayoutBuilder,
      context: Context,
      @AttrRes styleAttr: Int,
      @StyleRes styleRes: Int,
  ) {
    updateFromStyleResource(builder, context, null, styleAttr, styleRes)
  }

  /**
   * Sets the values for a TextLayoutBuilder from a style resource or a themed attribute.
   *
   * @param builder The TextLayoutBuilder
   * @param context The Context to use for resolving the attributes
   * @param attrs The [AttributeSet] used during inflation
   * @param styleAttr The themed style attribute
   * @param styleRes The style resource identifier
   */
  @JvmStatic
  fun updateFromStyleResource(
      builder: TextLayoutBuilder,
      context: Context,
      attrs: AttributeSet?,
      @AttrRes styleAttr: Int,
      @StyleRes styleRes: Int,
  ) {
    val customAttrs =
        context.obtainStyledAttributes(attrs, R.styleable.TextStyle, styleAttr, styleRes)

    val textAppearanceId =
        customAttrs.getResourceId(R.styleable.TextStyle_android_textAppearance, -1)

    if (textAppearanceId > 0) {
      setTextAppearance(builder, context, textAppearanceId)
    }

    val textColor = customAttrs.getColorStateList(R.styleable.TextStyle_android_textColor)

    val textSize =
        customAttrs.getDimensionPixelSize(
            R.styleable.TextStyle_android_textSize,
            DEFAULT_TEXT_SIZE_PX,
        )

    val shadowColor =
        customAttrs.getInt(R.styleable.TextStyle_android_shadowColor, Color.TRANSPARENT)

    val dx = customAttrs.getFloat(R.styleable.TextStyle_android_shadowDx, 0.0f)

    val dy = customAttrs.getFloat(R.styleable.TextStyle_android_shadowDy, 0.0f)

    val radius = customAttrs.getFloat(R.styleable.TextStyle_android_shadowRadius, 0.0f)

    val textStyle = customAttrs.getInt(R.styleable.TextStyle_android_textStyle, -1)

    val ellipsize = customAttrs.getInt(R.styleable.TextStyle_android_ellipsize, 0)

    val singleLine = customAttrs.getBoolean(R.styleable.TextStyle_android_singleLine, false)

    val maxLines =
        customAttrs.getInt(
            R.styleable.TextStyle_android_maxLines,
            TextLayoutBuilder.DEFAULT_MAX_LINES,
        )

    val breakStrategy = customAttrs.getInt(R.styleable.TextStyle_android_breakStrategy, -1)

    val hyphenationFrequency =
        customAttrs.getInt(R.styleable.TextStyle_android_hyphenationFrequency, -1)

    customAttrs.recycle()

    builder.setTextColor(textColor)

    builder.setTextSize(textSize)
    builder.setShadowLayer(radius, dx, dy, shadowColor)

    if (textStyle != -1) {
      builder.setTypeface(Typeface.defaultFromStyle(textStyle))
    } else {
      builder.setTypeface(null)
    }

    if (ellipsize > 0 && ellipsize < 4) {
      // TruncateAt doesn't have a value for NONE.
      builder.setEllipsize(TruncateAt.entries.toTypedArray().get(ellipsize - 1))
    } else {
      builder.setEllipsize(null)
    }

    builder.setSingleLine(singleLine)
    builder.setMaxLines(maxLines)

    if (breakStrategy > -1) {
      builder.setBreakStrategy(breakStrategy)
    }

    if (hyphenationFrequency > -1) {
      builder.setHyphenationFrequency(hyphenationFrequency)
    }
  }

  /**
   * Sets a text appearance for the layout.
   *
   * @param builder The [TextLayoutBuilder] instance
   * @param context The [Context] to use for resolving attributes
   * @param resId The resource identifier of the text appearance
   */
  @JvmStatic
  fun setTextAppearance(builder: TextLayoutBuilder, context: Context, @StyleRes resId: Int) {
    val customAttrs = context.obtainStyledAttributes(resId, R.styleable.TextAppearance)

    val textColor = customAttrs.getColorStateList(R.styleable.TextAppearance_android_textColor)

    val textSize = customAttrs.getDimensionPixelSize(R.styleable.TextAppearance_android_textSize, 0)

    val shadowColor =
        customAttrs.getInt(R.styleable.TextAppearance_android_shadowColor, Color.TRANSPARENT)

    if (shadowColor != Color.TRANSPARENT) {
      val dx = customAttrs.getFloat(R.styleable.TextAppearance_android_shadowDx, 0.0f)

      val dy = customAttrs.getFloat(R.styleable.TextAppearance_android_shadowDy, 0.0f)

      val radius = customAttrs.getFloat(R.styleable.TextAppearance_android_shadowRadius, 0.0f)

      builder.setShadowLayer(radius, dx, dy, shadowColor)
    }

    val textStyle = customAttrs.getInt(R.styleable.TextAppearance_android_textStyle, -1)

    customAttrs.recycle()

    // Override the color only if available.
    if (textColor != null) {
      builder.setTextColor(textColor)
    }

    if (textSize != 0) {
      builder.setTextSize(textSize)
    }

    // Override the style only if available.
    if (textStyle != -1) {
      builder.setTypeface(Typeface.defaultFromStyle(textStyle))
    }
  }
}
