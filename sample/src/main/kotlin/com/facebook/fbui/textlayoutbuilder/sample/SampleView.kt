/**
 * Copyright 2016-present, Facebook, Inc.
 * All rights reserved.
 * 
 * This source code is licensed under the license found in the
 * LICENSE-examples file in the root directory of this source tree.
 */
package com.facebook.fbui.textlayoutbuilder.sample

import android.content.Context
import android.graphics.Canvas
import android.view.View
import android.text.Layout

class SampleView(context: Context, private val layout: Layout) : View(context) {
  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    layout.draw(canvas)
  }
}
