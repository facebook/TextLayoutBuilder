/*
 * Copyright 2016-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.fbui.textlayoutbuilder.util;

import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;

/** Utility Class for measuring text {@link Layout}s. */
public class LayoutMeasureUtil {

  /**
   * Returns the width of the layout.
   *
   * @param layout The layout
   * @return The width of the layout
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
   * Prior to version 20, If the Layout specifies extra space between lines (either by spacingmult
   * or spacingadd) the StaticLayout would erroneously add this space after the last line as well.
   * This bug was fixed in version 20. This method calculates the extra space and reduces the height
   * by that amount.
   *
   * @param layout The layout
   * @return The height of the layout
   */
  public static int getHeight(Layout layout) {
    if (layout == null) {
      return 0;
    }

    int extra = 0;
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH
        && layout instanceof StaticLayout) {
      int above = layout.getLineAscent(layout.getLineCount() - 1);
      int below = layout.getLineDescent(layout.getLineCount() - 1);
      float originalSize = (below - above - layout.getSpacingAdd()) / layout.getSpacingMultiplier();
      float ex = below - above - originalSize;
      if (ex >= 0) {
        extra = (int) (ex + 0.5);
      } else {
        extra = -(int) (-ex + 0.5);
      }
    }
    return layout.getHeight() - extra;
  }
}
