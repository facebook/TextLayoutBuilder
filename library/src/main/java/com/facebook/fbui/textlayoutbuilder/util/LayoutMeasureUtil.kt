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

package com.facebook.fbui.textlayoutbuilder.util

import android.text.Layout

/** Utility Class for measuring text [Layout]s. */
object LayoutMeasureUtil {

  /**
   * Returns the width of the layout.
   *
   * @param layout The layout.
   * @return The width of the layout.
   */
  @JvmStatic
  fun getWidth(layout: Layout?): Int {
    if (layout == null) {
      return 0
    }

    // Supplying VERY_WIDE will make layout.getWidth() return a very large value.
    val count = layout.lineCount
    var maxWidth = 0

    for (i in 0 until count) {
      maxWidth = maxOf(maxWidth, layout.getLineRight(i).toInt())
    }

    return maxWidth
  }

  /**
   * Returns the height of the layout.
   *
   * @param layout The layout.
   * @return The height of the layout.
   */
  @JvmStatic
  fun getHeight(layout: Layout?): Int {
    if (layout == null) {
      return 0
    }

    return layout.height
  }

  /**
   * Returns the leftmost position of the layout. This is helpful when there's space between the
   * layout's left bound and its content's (lines) leftmost bound, e.g. StaticLayout when the text
   * alignment is not ALIGN_NORMAL.
   */
  @JvmStatic
  fun getContentLeft(layout: Layout?): Int {
    if (layout == null || layout.lineCount == 0) {
      return 0
    }

    var left = Int.MAX_VALUE
    for (i in 0 until layout.lineCount) {
      left = minOf(left, layout.getLineLeft(i).toInt())
    }

    return left
  }
}
