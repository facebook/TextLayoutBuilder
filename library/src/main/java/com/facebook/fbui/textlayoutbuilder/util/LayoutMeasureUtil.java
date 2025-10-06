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

package com.facebook.fbui.textlayoutbuilder.util;

import android.text.Layout;
import androidx.annotation.Nullable;

/** Utility Class for measuring text {@link Layout}s. */
public class LayoutMeasureUtil {

  /**
   * Returns the width of the layout.
   *
   * @param layout The layout.
   * @return The width of the layout.
   */
  public static int getWidth(Layout layout) {
    if (layout == null) {
      return 0;
    }

    // Supplying VERY_WIDE will make layout.getWidth() return a very large value.
    int count = layout.getLineCount();
    int maxWidth = 0;

    for (int i = 0; i < count; i++) {
      maxWidth = Math.max(maxWidth, (int) layout.getLineRight(i));
    }

    return maxWidth;
  }

  /**
   * Returns the height of the layout.
   *
   * @param layout The layout.
   * @return The height of the layout.
   */
  public static int getHeight(@Nullable Layout layout) {
    if (layout == null) {
      return 0;
    }

    return layout.getHeight();
  }

  /**
   * Returns the leftmost position of the layout. This is helpful when there's space between the
   * layout's left bound and its content's (lines) leftmost bound, e.g. StaticLayout when the text
   * alignment is not ALIGN_NORMAL.
   */
  public static int getContentLeft(Layout layout) {
    if (layout == null || layout.getLineCount() == 0) {
      return 0;
    }

    int left = Integer.MAX_VALUE;
    for (int i = 0; i < layout.getLineCount(); i++) {
      left = Math.min(left, (int) layout.getLineLeft(i));
    }

    return left;
  }
}
