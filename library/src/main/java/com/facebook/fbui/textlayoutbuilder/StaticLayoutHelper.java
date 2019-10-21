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

import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import androidx.core.text.TextDirectionHeuristicCompat;
import com.facebook.fbui.textlayoutbuilder.proxy.StaticLayoutProxy;
import java.lang.reflect.Field;

/** Helper class to get around the {@link StaticLayout} constructor limitation in ICS. */
/* package */ class StaticLayoutHelper {

  // Space and ellipsis to append at the end of a string to ellipsize it
  private static final String SPACE_AND_ELLIPSIS = " \u2026";

  /**
   * Returns a StaticLayout using ICS specific constructor if possible.
   *
   * @param text The text for the layout
   * @param start The start index
   * @param end The end index
   * @param paint The {@link TextPaint} to be used
   * @param width The width of the layout
   * @param alignment The {@link Layout.Alignment}
   * @param spacingMult The line spacing multiplier
   * @param spacingAdd The line spacing extra
   * @param includePadding Whether to include font padding
   * @param ellipsize The ellipsizing behavior specified by {@link TextUtils.TruncateAt}
   * @param ellipsisWidth The width of the ellipsis
   * @param maxLines The maximum number of lines for this layout
   * @param textDirection The text direction
   * @return A {@link StaticLayout}
   */
  private static StaticLayout getStaticLayoutMaybeMaxLines(
      CharSequence text,
      int start,
      int end,
      TextPaint paint,
      int width,
      Layout.Alignment alignment,
      float spacingMult,
      float spacingAdd,
      boolean includePadding,
      TextUtils.TruncateAt ellipsize,
      int ellipsisWidth,
      int maxLines,
      TextDirectionHeuristicCompat textDirection) {
    try {
      return StaticLayoutProxy.create(
          text,
          start,
          end,
          paint,
          width,
          alignment,
          spacingMult,
          spacingAdd,
          includePadding,
          ellipsize,
          ellipsisWidth,
          maxLines,
          textDirection);
    } catch (LinkageError e) {
      // Use the publicly available constructor.
    }

    return getStaticLayoutNoMaxLines(
        text,
        start,
        end,
        paint,
        width,
        alignment,
        spacingMult,
        spacingAdd,
        includePadding,
        ellipsize,
        ellipsisWidth);
  }

  /**
   * Returns a StaticLayout with no maxLines restriction.
   *
   * @param text The text for the layout
   * @param start The start index
   * @param end The end index
   * @param paint The {@link TextPaint} to be used
   * @param width The width of the layout
   * @param alignment The {@link Layout.Alignment}
   * @param spacingMult The line spacing multiplier
   * @param spacingAdd The line spacing extra
   * @param includePadding Whether to include font padding
   * @param ellipsize The ellipsizing behavior specified by {@link TextUtils.TruncateAt}
   * @param ellipsisWidth The width of the ellipsis
   * @return A {@link StaticLayout} with no maxLines restriction
   */
  private static StaticLayout getStaticLayoutNoMaxLines(
      CharSequence text,
      int start,
      int end,
      TextPaint paint,
      int width,
      Layout.Alignment alignment,
      float spacingMult,
      float spacingAdd,
      boolean includePadding,
      TextUtils.TruncateAt ellipsize,
      int ellipsisWidth) {

    return new StaticLayout(
        text,
        start,
        end,
        paint,
        width,
        alignment,
        spacingMult,
        spacingAdd,
        includePadding,
        ellipsize,
        ellipsisWidth);
  }

  /**
   * Creates a StaticLayout will all the required properties.
   *
   * @param text The text for the layout
   * @param start The start index
   * @param end The end index
   * @param paint The {@link TextPaint} to be used
   * @param width The width of the layout
   * @param alignment The {@link Layout.Alignment}
   * @param spacingMult The line spacing multiplier
   * @param spacingAdd The line spacing extra
   * @param includePadding Whether to include font padding
   * @param ellipsize The ellipsizing behavior specified by {@link TextUtils.TruncateAt}
   * @param ellipsisWidth The width of the ellipsis
   * @param maxLines The maximum number of lines for this layout
   * @param textDirection The text direction
   * @param breakStrategy The break strategy
   * @param hyphenationFrequency The hyphenation frequency
   * @param leftIndents The array of left indent margins in pixels
   * @param rightIndents The array of left indent margins in pixels
   * @param useLineSpacingFromFallbacks Whether to use the fallback font's line spacing
   * @return A {@link StaticLayout}
   */
  public static StaticLayout make(
      CharSequence text,
      int start,
      int end,
      TextPaint paint,
      int width,
      Layout.Alignment alignment,
      float spacingMult,
      float spacingAdd,
      boolean includePadding,
      TextUtils.TruncateAt ellipsize,
      int ellipsisWidth,
      int maxLines,
      TextDirectionHeuristicCompat textDirection,
      int breakStrategy,
      int hyphenationFrequency,
      int justificationMode,
      int[] leftIndents,
      int[] rightIndents,
      boolean useLineSpacingFromFallbacks) {

    if (Build.VERSION.SDK_INT >= 23) {
      StaticLayout.Builder builder =
          StaticLayout.Builder.obtain(text, start, end, paint, width)
              .setAlignment(alignment)
              .setLineSpacing(spacingAdd, spacingMult)
              .setIncludePad(includePadding)
              .setEllipsize(ellipsize)
              .setEllipsizedWidth(ellipsisWidth)
              .setMaxLines(maxLines)
              .setTextDirection(StaticLayoutProxy.fromTextDirectionHeuristicCompat(textDirection))
              .setBreakStrategy(breakStrategy)
              .setHyphenationFrequency(hyphenationFrequency)
              .setIndents(leftIndents, rightIndents);

      if (Build.VERSION.SDK_INT >= 26) {
        builder.setJustificationMode(justificationMode);
      }

      if (Build.VERSION.SDK_INT >= 28) {
        builder.setUseLineSpacingFromFallbacks(useLineSpacingFromFallbacks);
      }

      return builder.build();
    }

    StaticLayout layout =
        getStaticLayoutMaybeMaxLines(
            text,
            start,
            end,
            paint,
            width,
            alignment,
            spacingMult,
            spacingAdd,
            includePadding,
            ellipsize,
            ellipsisWidth,
            maxLines,
            textDirection);

    // Returned layout may not have correct line count (either because it is not supported
    // pre-ICS, or because there is a bug in Android pre-Lollipop that causes the text to span
    // over more lines than we asked for). We need to manually check if that happened and
    // re-create Layout with a substring that will fit into required number of lines.
    if (maxLines > 0) {
      while (layout.getLineCount() > maxLines) {
        int newEnd = layout.getLineStart(maxLines);
        if (newEnd >= end) {
          // to break out of a potential infinite loop
          break;
        }

        // newEnd is where the next line starts, not where the previous line ends
        // we need to skip over the whitespace characters to get to the end of the line
        while (newEnd > start) {
          if (Character.isSpace(text.charAt(newEnd - 1))) {
            --newEnd;
          } else {
            break;
          }
        }

        end = newEnd;

        layout =
            getStaticLayoutMaybeMaxLines(
                text,
                start,
                end,
                paint,
                width,
                alignment,
                spacingMult,
                spacingAdd,
                includePadding,
                ellipsize,
                ellipsisWidth,
                maxLines,
                textDirection);

        if (layout.getLineCount() >= maxLines && layout.getEllipsisCount(maxLines - 1) == 0) {
          CharSequence ellipsizedText = text.subSequence(start, end) + SPACE_AND_ELLIPSIS;
          layout =
              getStaticLayoutMaybeMaxLines(
                  ellipsizedText,
                  0,
                  ellipsizedText.length(),
                  paint,
                  width,
                  alignment,
                  spacingMult,
                  spacingAdd,
                  includePadding,
                  ellipsize,
                  ellipsisWidth,
                  maxLines,
                  textDirection);
        }
      }
    }

    while (!fixLayout(layout)) {
      // try again
    }

    return layout;
  }

  /**
   * Attempts to fix a StaticLayout with wrong layout information that can result in
   * StringIndexOutOfBoundsException during layout.draw().
   *
   * @param layout The {@link StaticLayout} to fix
   * @return Whether the layout was fixed or not
   */
  public static boolean fixLayout(StaticLayout layout) {
    int lineStart = layout.getLineStart(0);
    for (int i = 0, lineCount = layout.getLineCount(); i < lineCount; ++i) {
      int lineEnd = layout.getLineEnd(i);
      if (lineEnd < lineStart) {
        // Bug, need to swap lineStart and lineEnd
        try {
          Field mLinesField = StaticLayout.class.getDeclaredField("mLines");
          mLinesField.setAccessible(true);

          Field mColumnsField = StaticLayout.class.getDeclaredField("mColumns");
          mColumnsField.setAccessible(true);

          int[] mLines = (int[]) mLinesField.get(layout);
          int mColumns = mColumnsField.getInt(layout);

          // swap lineStart and lineEnd by swapping all the following data:
          // mLines[mColumns * i.. mColumns * i+1] <-> mLines[mColumns * (i+1)..mColumns * (i+2)]
          for (int j = 0; j < mColumns; ++j) {
            swap(mLines, mColumns * i + j, mColumns * i + j + mColumns);
          }
        } catch (Exception e) {
          // something is wrong, bail out
          break;
        }

        // start over
        return false;
      }

      lineStart = lineEnd;
    }

    return true;
  }

  private static void swap(int[] array, int i, int j) {
    int tmp = array[i];
    array[i] = array[j];
    array[j] = tmp;
  }
}
