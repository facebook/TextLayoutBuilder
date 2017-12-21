/**
 * Copyright (c) 2016-present, Facebook, Inc. All rights reserved.
 *
 * <p>This source code is licensed under the BSD-style license found in the LICENSE file in the root
 * directory of this source tree. An additional grant of patent rights can be found in the PATENTS
 * file in the same directory.
 */
package com.facebook.fbui.textlayoutbuilder.shadows;

import android.text.Layout;
import android.text.TextPaint;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(Layout.class)
public class ShadowLayout {

  public static final String LONG_TEXT = "ZzZzZzZzZzZzZzZzZzZzZz";
  public static final String SHORT_TEXT = "Z";
  public static final int LONG_TEXT_LENGTH = 100;
  public static final int SHORT_TEXT_LENGTH = 5;

  @Implementation
  public static float getDesiredWidth(CharSequence source, TextPaint paint) {
    if (LONG_TEXT.equals(source)) {
      return LONG_TEXT_LENGTH;
    } else if (SHORT_TEXT.equals(source)) {
      return SHORT_TEXT_LENGTH;
    }
    return 0;
  }
}
