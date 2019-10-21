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

package com.facebook.fbui.textlayoutbuilder.proxy;

import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils;
import androidx.core.text.TextDirectionHeuristicCompat;
import androidx.core.text.TextDirectionHeuristicsCompat;

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
