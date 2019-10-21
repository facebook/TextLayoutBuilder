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

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import androidx.annotation.AttrRes;
import androidx.annotation.StyleRes;

/** An utility class to update a {@link TextLayoutBuilder} from an Android resource. */
public class ResourceTextLayoutHelper {

  // Font size in pixels.
  private static final int DEFAULT_TEXT_SIZE_PX = 15;

  /**
   * Sets the values for a TextLayoutBuilder from a style resource.
   *
   * @param builder The TextLayoutBuilder
   * @param context The Context to use for resolving the attributes
   * @param styleRes The style resource identifier
   */
  public static void updateFromStyleResource(
      TextLayoutBuilder builder, Context context, @StyleRes int styleRes) {
    updateFromStyleResource(builder, context, 0, styleRes);
  }

  /**
   * Sets the values for a TextLayoutBuilder from a style resource or a themed attribute.
   *
   * @param builder The TextLayoutBuilder
   * @param context The Context to use for resolving the attributes
   * @param styleAttr The themed style attribute
   * @param styleRes The style resource identifier
   */
  public static void updateFromStyleResource(
      TextLayoutBuilder builder, Context context, @AttrRes int styleAttr, @StyleRes int styleRes) {
    updateFromStyleResource(builder, context, null, styleAttr, styleRes);
  }

  /**
   * Sets the values for a TextLayoutBuilder from a style resource or a themed attribute.
   *
   * @param builder The TextLayoutBuilder
   * @param context The Context to use for resolving the attributes
   * @param attrs The {@link AttributeSet} used during inflation
   * @param styleAttr The themed style attribute
   * @param styleRes The style resource identifier
   */
  public static void updateFromStyleResource(
      TextLayoutBuilder builder,
      Context context,
      AttributeSet attrs,
      @AttrRes int styleAttr,
      @StyleRes int styleRes) {
    TypedArray customAttrs =
        context.obtainStyledAttributes(attrs, R.styleable.TextStyle, styleAttr, styleRes);

    int textAppearanceId =
        customAttrs.getResourceId(R.styleable.TextStyle_android_textAppearance, -1);

    if (textAppearanceId > 0) {
      setTextAppearance(builder, context, textAppearanceId);
    }

    ColorStateList textColor =
        customAttrs.getColorStateList(R.styleable.TextStyle_android_textColor);

    int textSize =
        customAttrs.getDimensionPixelSize(
            R.styleable.TextStyle_android_textSize, DEFAULT_TEXT_SIZE_PX);

    int shadowColor =
        customAttrs.getInt(R.styleable.TextStyle_android_shadowColor, Color.TRANSPARENT);

    float dx = customAttrs.getFloat(R.styleable.TextStyle_android_shadowDx, 0.0f);

    float dy = customAttrs.getFloat(R.styleable.TextStyle_android_shadowDy, 0.0f);

    float radius = customAttrs.getFloat(R.styleable.TextStyle_android_shadowRadius, 0.0f);

    int textStyle = customAttrs.getInt(R.styleable.TextStyle_android_textStyle, -1);

    int ellipsize = customAttrs.getInt(R.styleable.TextStyle_android_ellipsize, 0);

    boolean singleLine = customAttrs.getBoolean(R.styleable.TextStyle_android_singleLine, false);

    int maxLines =
        customAttrs.getInt(
            R.styleable.TextStyle_android_maxLines, TextLayoutBuilder.DEFAULT_MAX_LINES);

    int breakStrategy = customAttrs.getInt(R.styleable.TextStyle_android_breakStrategy, -1);

    int hyphenationFrequency =
        customAttrs.getInt(R.styleable.TextStyle_android_hyphenationFrequency, -1);

    customAttrs.recycle();

    builder.setTextColor(textColor);

    builder.setTextSize(textSize);
    builder.setShadowLayer(radius, dx, dy, shadowColor);

    if (textStyle != -1) {
      builder.setTypeface(Typeface.defaultFromStyle(textStyle));
    } else {
      builder.setTypeface(null);
    }

    if (ellipsize > 0 && ellipsize < 4) {
      // TruncateAt doesn't have a value for NONE.
      builder.setEllipsize(TextUtils.TruncateAt.values()[ellipsize - 1]);
    } else {
      builder.setEllipsize(null);
    }

    builder.setSingleLine(singleLine);
    builder.setMaxLines(maxLines);

    if (breakStrategy > -1) {
      builder.setBreakStrategy(breakStrategy);
    }

    if (hyphenationFrequency > -1) {
      builder.setHyphenationFrequency(hyphenationFrequency);
    }
  }

  /**
   * Sets a text appearance for the layout.
   *
   * @param builder The {@link TextLayoutBuilder} instance
   * @param context The {@link Context} to use for resolving attributes
   * @param resId The resource identifier of the text appearance
   */
  public static void setTextAppearance(
      TextLayoutBuilder builder, Context context, @StyleRes int resId) {
    TypedArray customAttrs = context.obtainStyledAttributes(resId, R.styleable.TextAppearance);

    ColorStateList textColor =
        customAttrs.getColorStateList(R.styleable.TextAppearance_android_textColor);

    int textSize =
        customAttrs.getDimensionPixelSize(R.styleable.TextAppearance_android_textSize, 0);

    int shadowColor =
        customAttrs.getInt(R.styleable.TextAppearance_android_shadowColor, Color.TRANSPARENT);

    if (shadowColor != Color.TRANSPARENT) {
      float dx = customAttrs.getFloat(R.styleable.TextAppearance_android_shadowDx, 0.0f);

      float dy = customAttrs.getFloat(R.styleable.TextAppearance_android_shadowDy, 0.0f);

      float radius = customAttrs.getFloat(R.styleable.TextAppearance_android_shadowRadius, 0.0f);

      builder.setShadowLayer(radius, dx, dy, shadowColor);
    }

    int textStyle = customAttrs.getInt(R.styleable.TextAppearance_android_textStyle, -1);

    customAttrs.recycle();

    // Override the color only if available.
    if (textColor != null) {
      builder.setTextColor(textColor);
    }

    if (textSize != 0) {
      builder.setTextSize(textSize);
    }

    // Override the style only if available.
    if (textStyle != -1) {
      builder.setTypeface(Typeface.defaultFromStyle(textStyle));
    }
  }
}
