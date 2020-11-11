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

package com.facebook.fbui.textlayoutbuilder.sample

import android.content.Context
import android.os.Bundle
import android.text.Layout
import android.util.TypedValue
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder

class MainActivity : AppCompatActivity() {
  private lateinit var parent: LinearLayout

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    parent = findViewById(R.id.parent)

    addSample { TextLayoutBuilder().setText("Hello, world!").setTextSize(20f.dp(this)).build() }
  }

  private fun addSample(block: () -> Layout?) {
    val layout = block() ?: return
    parent.addView(
        SampleView(this, layout).apply {
          layoutParams = LinearLayout.LayoutParams(layout.width, layout.height)
        })
  }

  private fun Float.dp(context: Context): Int {
    return Math.round(
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics))
  }
}
