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

package com.facebook.fbui.textlayoutbuilder.glyphwarmer;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.Layout;
import androidx.annotation.VisibleForTesting;
import com.facebook.fbui.textlayoutbuilder.GlyphWarmer;
import com.facebook.fbui.textlayoutbuilder.util.LayoutMeasureUtil;

/**
 * Default {@link GlyphWarmer} that runs a {@link HandlerThread} to draw a text {@link Layout} on a
 * {@link Picture}. This helps in warming up the FreeType cache in Android 4.0+.
 */
public class GlyphWarmerImpl implements GlyphWarmer {

  // Handler for the HandlerThread.
  private static WarmHandler sWarmHandler;

  @Override
  public void warmLayout(Layout layout) {
    WarmHandler handler = getWarmHandler();
    handler.sendMessage(handler.obtainMessage(WarmHandler.NO_OP, layout));
  }

  @VisibleForTesting
  Looper getWarmHandlerLooper() {
    return getWarmHandler().getLooper();
  }

  @SuppressLint({
    "BadMethodUse-android.os.HandlerThread._Constructor",
    "BadMethodUse-java.lang.Thread.start"
  })
  private WarmHandler getWarmHandler() {
    if (sWarmHandler == null) {
      // A text warmer thread to render the the layout in the background.
      // This helps warm the layout cache in Android 4.0+ devices,
      // making large blurbs of text to draw in 0ms.
      HandlerThread warmerThread = new HandlerThread("GlyphWarmer");
      warmerThread.start();

      // Note: warmerThread.getLooper() can return null.
      sWarmHandler = new WarmHandler(warmerThread.getLooper());
    }

    return sWarmHandler;
  }

  /** A handler to send messages to the GlyphWarmerImpl thread. */
  private static class WarmHandler extends Handler {

    private static final int NO_OP = 1;

    private final Picture mPicture = new Picture();

    WarmHandler(Looper looper) {
      super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
      Layout layout = (Layout) msg.obj;
      try {
        Canvas canvas =
            mPicture.beginRecording(
                LayoutMeasureUtil.getWidth(layout), LayoutMeasureUtil.getHeight(layout));
        layout.draw(canvas);
        mPicture.endRecording();
      } catch (Exception e) {
        // Do nothing.
      }
    }
  }
}
