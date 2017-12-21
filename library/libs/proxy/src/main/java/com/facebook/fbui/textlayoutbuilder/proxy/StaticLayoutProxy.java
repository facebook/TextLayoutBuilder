/**
 * Copyright (c) 2016-present, Facebook, Inc. All rights reserved.
 *
 * <p>This source code is licensed under the BSD-style license found in the LICENSE file in the root
 * directory of this source tree. An additional grant of patent rights can be found in the PATENTS
 * file in the same directory.
 */
package com.facebook.fbui.textlayoutbuilder.proxy;

import android.support.v4.text.TextDirectionHeuristicCompat;
import android.support.v4.text.TextDirectionHeuristicsCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils;

public class StaticLayoutProxy {
  public static StaticLayout create(
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
      return new StaticLayout(
          text,
          start,
          end,
          paint,
          width,
          alignment,
          fromTextDirectionHeuristicCompat(textDirection),
          spacingMult,
          spacingAdd,
          includePadding,
          ellipsize,
          ellipsisWidth,
          maxLines);
    } catch (IllegalArgumentException e) {
      // Retry creating the layout if the first attempt failed due to a race condition.
      // See https://code.google.com/p/android/issues/detail?id=188163
      if (e.getMessage().contains("utext_close")) {
        return new StaticLayout(
            text,
            start,
            end,
            paint,
            width,
            alignment,
            fromTextDirectionHeuristicCompat(textDirection),
            spacingMult,
            spacingAdd,
            includePadding,
            ellipsize,
            ellipsisWidth,
            maxLines);
      }
      throw e;
    }
  }

  public static TextDirectionHeuristic fromTextDirectionHeuristicCompat(
      TextDirectionHeuristicCompat textDirection) {
    if (textDirection == TextDirectionHeuristicsCompat.LTR) {
      return TextDirectionHeuristics.LTR;
    } else if (textDirection == TextDirectionHeuristicsCompat.RTL) {
      return TextDirectionHeuristics.RTL;
    } else if (textDirection == TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR) {
      return TextDirectionHeuristics.FIRSTSTRONG_LTR;
    } else if (textDirection == TextDirectionHeuristicsCompat.FIRSTSTRONG_RTL) {
      return TextDirectionHeuristics.FIRSTSTRONG_RTL;
    } else if (textDirection == TextDirectionHeuristicsCompat.ANYRTL_LTR) {
      return TextDirectionHeuristics.ANYRTL_LTR;
    } else if (textDirection == TextDirectionHeuristicsCompat.LOCALE) {
      return TextDirectionHeuristics.LOCALE;
    } else {
      return TextDirectionHeuristics.FIRSTSTRONG_LTR;
    }
  }
}
