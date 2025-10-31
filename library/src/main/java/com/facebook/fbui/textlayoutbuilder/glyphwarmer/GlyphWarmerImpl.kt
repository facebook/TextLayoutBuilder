/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
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

package com.facebook.fbui.textlayoutbuilder.glyphwarmer

import android.annotation.SuppressLint
import android.graphics.Picture
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.text.Layout
import androidx.annotation.VisibleForTesting
import com.facebook.fbui.textlayoutbuilder.GlyphWarmer
import com.facebook.fbui.textlayoutbuilder.util.LayoutMeasureUtil
import com.google.common.base.Preconditions

/**
 * Default [GlyphWarmer] that runs a [HandlerThread] to draw a text [Layout] on a [Picture]. This
 * helps in warming up the FreeType cache in Android 4.0+.
 */
class GlyphWarmerImpl : GlyphWarmer {

  override fun warmLayout(layout: Layout?) {
    val handler = getWarmHandler()
    handler.sendMessage(handler.obtainMessage(WarmHandler.NO_OP, layout))
  }

  @VisibleForTesting
  fun getWarmHandlerLooper(): Looper {
    return getWarmHandler().looper
  }

  @SuppressLint(
      "BadMethodUse-android.os.HandlerThread._Constructor",
      "BadMethodUse-java.lang.Thread.start",
  )
  private fun getWarmHandler(): WarmHandler {
    return warmHandler
        ?: run {
          // A text warmer thread to render the the layout in the background.
          // This helps warm the layout cache in Android 4.0+ devices,
          // making large blurbs of text to draw in 0ms.
          val warmerThread = HandlerThread("GlyphWarmer")
          warmerThread.start()

          // Note: warmerThread.getLooper() can return null.
          WarmHandler(warmerThread.looper).also { warmHandler = it }
        }
  }

  /** A handler to send messages to the GlyphWarmerImpl thread. */
  private class WarmHandler(looper: Looper) : Handler(looper) {

    private val picture = Picture()

    override fun handleMessage(msg: Message) {
      try {
        val layout = msg.obj as? Layout ?: return
        val canvas =
            picture.beginRecording(
                LayoutMeasureUtil.getWidth(layout),
                LayoutMeasureUtil.getHeight(layout),
            )
        Preconditions.checkNotNull(layout).draw(canvas)
        picture.endRecording()
      } catch (e: Exception) {
        // Do nothing.
      }
    }

    companion object {
      const val NO_OP = 1
    }
  }

  companion object {
    // Handler for the HandlerThread.
    private var warmHandler: WarmHandler? = null
  }
}
