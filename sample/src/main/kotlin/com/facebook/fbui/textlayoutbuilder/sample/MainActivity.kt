/**
 * Copyright 2016-present, Facebook, Inc.
 * All rights reserved.
 * 
 * This source code is licensed under the license found in the
 * LICENSE-examples file in the root directory of this source tree.
 */
package com.facebook.fbui.textlayoutbuilder.sample

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Layout
import android.util.TypedValue
import android.widget.LinearLayout
import com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder

class MainActivity : AppCompatActivity() {
  private lateinit var parent: LinearLayout

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    parent = findViewById(R.id.parent)

    addSample {
      TextLayoutBuilder().setText("Hello, world!").setTextSize(20f.dp(this)).build()
    }
  }

  private fun addSample(block: () -> Layout?) {
    val layout = block() ?: return
    parent.addView(SampleView(this, layout).apply {
      layoutParams = LinearLayout.LayoutParams(layout.width, layout.height)
    })
  }

  private fun Float.dp(context: Context): Int {
    return Math.round(
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics))
  }
}
