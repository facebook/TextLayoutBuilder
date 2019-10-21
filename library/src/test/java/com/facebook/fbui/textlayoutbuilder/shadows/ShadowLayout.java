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
