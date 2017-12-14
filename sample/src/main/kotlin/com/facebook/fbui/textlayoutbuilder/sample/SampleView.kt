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
