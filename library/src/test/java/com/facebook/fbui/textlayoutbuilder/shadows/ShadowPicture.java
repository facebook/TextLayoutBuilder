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
  public void __constructor__(long nativePicture) {
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
