/**
 * Copyright (c) 2016-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.facebook.fbui.textlayoutbuilder.proxy;

import java.lang.CharSequence;

import android.support.v4.text.TextDirectionHeuristicCompat;
import android.support.v4.text.TextDirectionHeuristicsCompat;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;

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

  private static TextDirectionHeuristic fromTextDirectionHeuristicCompat(
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
