/**
 * Copyright (c) 2016-present, Facebook, Inc. All rights reserved.
 *
 * <p>This source code is licensed under the BSD-style license found in the LICENSE file in the root
 * directory of this source tree. An additional grant of patent rights can be found in the PATENTS
 * file in the same directory.
 */
package com.facebook.fbui.textlayoutbuilder.shadows;

import android.graphics.Canvas;
import android.graphics.Picture;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(Picture.class)
public class ShadowPicture {

  @Implementation
  public void __constructor__(int nativePicture, boolean fromStream) {
    // Do nothing.
  }

  @Implementation
  public void __constructor__(int nativePicture) {
    // Do nothing.
  }

  @Implementation
  public void __constructor__() {
    // Do nothing.
  }

  @Implementation
  public Canvas beginRecording(int width, int height) {
    return new Canvas();
  }
}
