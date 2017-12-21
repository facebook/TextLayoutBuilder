/**
 * Copyright (c) 2016-present, Facebook, Inc. All rights reserved.
 *
 * <p>This source code is licensed under the BSD-style license found in the LICENSE file in the root
 * directory of this source tree. An additional grant of patent rights can be found in the PATENTS
 * file in the same directory.
 */
package com.facebook.fbui.textlayoutbuilder.glyphwarmer;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.VisibleForTesting;
import android.text.Layout;
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

    public WarmHandler(Looper looper) {
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
