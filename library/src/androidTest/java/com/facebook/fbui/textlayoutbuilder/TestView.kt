/**
 * Copyright (c) 2016-present, Facebook, Inc. All rights reserved.
 *
 * <p>This source code is licensed under the BSD-style license found in the LICENSE file in the root
 * directory of this source tree. An additional grant of patent rights can be found in the PATENTS
 * file in the same directory.
 */
package com.facebook.fbui.textlayoutbuilder

import android.content.Context
import android.graphics.Canvas
import android.text.Layout
import android.view.View

class TestView(context: Context, private val layout: Layout) : View(context) {
  override fun onDraw(canvas: Canvas) {
    layout.draw(canvas)
  }
}
