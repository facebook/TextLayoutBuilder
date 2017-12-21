/**
 * Copyright (c) 2016-present, Facebook, Inc. All rights reserved.
 *
 * <p>This source code is licensed under the BSD-style license found in the LICENSE file in the root
 * directory of this source tree. An additional grant of patent rights can be found in the PATENTS
 * file in the same directory.
 */
package com.facebook.fbui.textlayoutbuilder;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import java.util.Arrays;

/** A comparable version of {@link TextPaint}. */
class TextLayoutPaint extends TextPaint {

  private float mShadowDx;
  private float mShadowDy;
  private float mShadowRadius;
  private int mShadowColor;

  public TextLayoutPaint() {
    super();
  }

  public TextLayoutPaint(int flags) {
    super(flags);
  }

  public TextLayoutPaint(Paint p) {
    super(p);
    if (p instanceof TextPaint) {
      // super doesn't copy TextPaint specific attributes
      set((TextPaint) p);
    }
  }

  @Override
  public void setShadowLayer(float radius, float dx, float dy, int color) {
    mShadowRadius = radius;
    mShadowDx = dx;
    mShadowDy = dy;
    mShadowColor = color;

    super.setShadowLayer(radius, dx, dy, color);
  }

  @Override
  public int hashCode() {
    Typeface tf = getTypeface();

    int hashCode = 1;
    hashCode = 31 * hashCode + getColor();
    hashCode = 31 * hashCode + Float.floatToIntBits(getTextSize());
    hashCode = 31 * hashCode + (tf != null ? tf.hashCode() : 0);
    hashCode = 31 * hashCode + Float.floatToIntBits(mShadowDx);
    hashCode = 31 * hashCode + Float.floatToIntBits(mShadowDy);
    hashCode = 31 * hashCode + Float.floatToIntBits(mShadowRadius);
    hashCode = 31 * hashCode + mShadowColor;
    hashCode = 31 * hashCode + linkColor;
    hashCode = 31 * hashCode + Float.floatToIntBits(density);
    hashCode = 31 * hashCode + Arrays.hashCode(drawableState);

    return hashCode;
  }
}
