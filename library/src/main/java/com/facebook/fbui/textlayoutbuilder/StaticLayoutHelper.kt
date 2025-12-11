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

import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils.TruncateAt
import androidx.core.text.TextDirectionHeuristicCompat
import com.facebook.fbui.textlayoutbuilder.proxy.StaticLayoutProxy

/** Helper class to get around the [StaticLayout] constructor limitation in ICS. */
/* package */

internal object StaticLayoutHelper {

  // Space and ellipsis to append at the end of a string to ellipsize it
  private const val SPACE_AND_ELLIPSIS = " \u2026"

  /**
   * Returns a StaticLayout using ICS specific constructor if possible.
   *
   * @param text The text for the layout
   * @param start The start index
   * @param end The end index
   * @param paint The [TextPaint] to be used
   * @param width The width of the layout
   * @param alignment The [Layout.Alignment]
   * @param spacingMult The line spacing multiplier
   * @param spacingAdd The line spacing extra
   * @param includePadding Whether to include font padding
   * @param ellipsize The ellipsizing behavior specified by [TextUtils.TruncateAt]
   * @param ellipsisWidth The width of the ellipsis
   * @param maxLines The maximum number of lines for this layout
   * @param textDirection The text direction
   * @return A [StaticLayout]
   */
  private fun getStaticLayoutMaybeMaxLines(
      text: CharSequence,
      start: Int,
      end: Int,
      paint: TextPaint,
      width: Int,
      alignment: Layout.Alignment,
      spacingMult: Float,
      spacingAdd: Float,
      includePadding: Boolean,
      ellipsize: TruncateAt?,
      ellipsisWidth: Int,
      maxLines: Int,
      textDirection: TextDirectionHeuristicCompat,
  ): StaticLayout {
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
          ellipsize!!,
          ellipsisWidth,
          maxLines,
          textDirection,
      )
    } catch (e: LinkageError) {
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
        ellipsisWidth,
    )
  }

  /**
   * Returns a StaticLayout with no maxLines restriction.
   *
   * @param text The text for the layout
   * @param start The start index
   * @param end The end index
   * @param paint The [TextPaint] to be used
   * @param width The width of the layout
   * @param alignment The [Layout.Alignment]
   * @param spacingMult The line spacing multiplier
   * @param spacingAdd The line spacing extra
   * @param includePadding Whether to include font padding
   * @param ellipsize The ellipsizing behavior specified by [TextUtils.TruncateAt]
   * @param ellipsisWidth The width of the ellipsis
   * @return A [StaticLayout] with no maxLines restriction
   */
  private fun getStaticLayoutNoMaxLines(
      text: CharSequence,
      start: Int,
      end: Int,
      paint: TextPaint,
      width: Int,
      alignment: Layout.Alignment,
      spacingMult: Float,
      spacingAdd: Float,
      includePadding: Boolean,
      ellipsize: TruncateAt?,
      ellipsisWidth: Int,
  ): StaticLayout =
      StaticLayout(
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
      )

  /**
   * Creates a StaticLayout will all the required properties.
   *
   * @param text The text for the layout
   * @param start The start index
   * @param end The end index
   * @param paint The [TextPaint] to be used
   * @param width The width of the layout
   * @param alignment The [Layout.Alignment]
   * @param spacingMult The line spacing multiplier
   * @param spacingAdd The line spacing extra
   * @param includePadding Whether to include font padding
   * @param ellipsize The ellipsizing behavior specified by [TextUtils.TruncateAt]
   * @param ellipsisWidth The width of the ellipsis
   * @param maxLines The maximum number of lines for this layout
   * @param textDirection The text direction
   * @param breakStrategy The break strategy
   * @param hyphenationFrequency The hyphenation frequency
   * @param leftIndents The array of left indent margins in pixels
   * @param rightIndents The array of left indent margins in pixels
   * @param useLineSpacingFromFallbacks Whether to use the fallback font's line spacing
   * @return A [StaticLayout]
   */
  @JvmStatic
  fun make(
      text: CharSequence,
      start: Int,
      end: Int,
      paint: TextPaint,
      width: Int,
      alignment: Layout.Alignment,
      spacingMult: Float,
      spacingAdd: Float,
      includePadding: Boolean,
      ellipsize: TruncateAt?,
      ellipsisWidth: Int,
      maxLines: Int,
      textDirection: TextDirectionHeuristicCompat,
      breakStrategy: Int,
      hyphenationFrequency: Int,
      justificationMode: Int,
      leftIndents: IntArray?,
      rightIndents: IntArray?,
      useLineSpacingFromFallbacks: Boolean,
  ): StaticLayout {
    var end = end
    if (Build.VERSION.SDK_INT >= 23) {
      val builder =
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
              .setIndents(leftIndents, rightIndents)

      if (Build.VERSION.SDK_INT >= 26) {
        builder.setJustificationMode(justificationMode)
      }

      if (Build.VERSION.SDK_INT >= 28) {
        builder.setUseLineSpacingFromFallbacks(useLineSpacingFromFallbacks)
      }

      return builder.build()
    }

    var layout =
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
            textDirection,
        )

    // Returned layout may not have correct line count (either because it is not supported
    // pre-ICS, or because there is a bug in Android pre-Lollipop that causes the text to span
    // over more lines than we asked for). We need to manually check if that happened and
    // re-create Layout with a substring that will fit into required number of lines.
    if (maxLines > 0) {
      while (layout.lineCount > maxLines) {
        var newEnd = layout.getLineStart(maxLines)
        if (newEnd >= end) {
          // to break out of a potential infinite loop
          break
        }

        // newEnd is where the next line starts, not where the previous line ends
        // we need to skip over the whitespace characters to get to the end of the line
        while (newEnd > start) {
          if (Character.isSpace(text[newEnd - 1])) {
            --newEnd
          } else {
            break
          }
        }

        end = newEnd

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
                textDirection,
            )

        if (layout.lineCount >= maxLines && layout.getEllipsisCount(maxLines - 1) == 0) {
          val ellipsizedText = text.subSequence(start, end).toString() + SPACE_AND_ELLIPSIS
          layout =
              getStaticLayoutMaybeMaxLines(
                  ellipsizedText,
                  0,
                  ellipsizedText.length,
                  paint,
                  width,
                  alignment,
                  spacingMult,
                  spacingAdd,
                  includePadding,
                  ellipsize,
                  ellipsisWidth,
                  maxLines,
                  textDirection,
              )
        }
      }
    }

    while (!fixLayout(layout)) {
      // try again
    }

    return layout
  }

  /**
   * Attempts to fix a StaticLayout with wrong layout information that can result in
   * StringIndexOutOfBoundsException during layout.draw().
   *
   * @param layout The [StaticLayout] to fix
   * @return Whether the layout was fixed or not
   */
  @JvmStatic
  fun fixLayout(layout: StaticLayout): Boolean {
    var lineStart = layout.getLineStart(0)
    var i = 0
    val lineCount = layout.lineCount
    while (i < lineCount) {
      val lineEnd = layout.getLineEnd(i)
      if (lineEnd < lineStart) {
        // Bug, need to swap lineStart and lineEnd
        try {
          val mLinesField = StaticLayout::class.java.getDeclaredField("mLines")
          mLinesField.isAccessible = true

          val mColumnsField = StaticLayout::class.java.getDeclaredField("mColumns")
          mColumnsField.isAccessible = true

          val mLines = mLinesField[layout] as IntArray?
          val mColumns = mColumnsField.getInt(layout)

          // swap lineStart and lineEnd by swapping all the following data:
          // mLines[mColumns * i.. mColumns * i+1] <-> mLines[mColumns * (i+1)..mColumns * (i+2)]
          for (j in 0..<mColumns) {
            swap(mLines!!, mColumns * i + j, mColumns * i + j + mColumns)
          }
        } catch (e: Exception) {
          // something is wrong, bail out
          break
        }

        // start over
        return false
      }

      lineStart = lineEnd
      ++i
    }

    return true
  }

  private fun swap(array: IntArray, i: Int, j: Int) {
    val tmp = array[i]
    array[i] = array[j]
    array[j] = tmp
  }
}
