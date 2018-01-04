/**
 * Copyright (c) 2016-present, Facebook, Inc. All rights reserved.
 *
 * <p>This source code is licensed under the BSD-style license found in the LICENSE file in the root
 * directory of this source tree. An additional grant of patent rights can be found in the PATENTS
 * file in the same directory.
 */
package com.facebook.fbui.textlayoutbuilder;

import android.text.Layout;

/**
 * Specifies an interface that a class has to implement to warm the text {@link Layout} in the
 * background. This approach helps in drawing text in post Android 4.0 devices.
 */
public interface GlyphWarmer {
  /**
   * Warms the text layout.
   *
   * @param layout The layout
   */
  void warmLayout(Layout layout);
}
