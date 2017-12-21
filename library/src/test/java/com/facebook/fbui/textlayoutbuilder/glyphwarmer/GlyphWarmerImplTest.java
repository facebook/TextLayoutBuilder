/**
 * Copyright (c) 2016-present, Facebook, Inc. All rights reserved.
 *
 * <p>This source code is licensed under the BSD-style license found in the LICENSE file in the root
 * directory of this source tree. An additional grant of patent rights can be found in the PATENTS
 * file in the same directory.
 */
package com.facebook.fbui.textlayoutbuilder.glyphwarmer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import android.graphics.Canvas;
import android.text.Layout;
import com.facebook.fbui.textlayoutbuilder.shadows.ShadowPicture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.internal.ShadowExtractor;
import org.robolectric.shadows.ShadowLooper;

/** Tests {@link GlyphWarmerImpl}. */
@Config(
  manifest = Config.NONE,
  shadows = {ShadowPicture.class}
)
@RunWith(RobolectricTestRunner.class)
public class GlyphWarmerImplTest {

  @Mock Layout mLayout;

  private ShadowLooper mShadowLooper;
  private GlyphWarmerImpl mGlyphWarmerImpl;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);

    mGlyphWarmerImpl = new GlyphWarmerImpl();
    mShadowLooper = (ShadowLooper) ShadowExtractor.extract(mGlyphWarmerImpl.getWarmHandlerLooper());
  }

  @Test
  public void testWarmGlyph() {
    mGlyphWarmerImpl.warmLayout(mLayout);
    mShadowLooper.runOneTask();
    verify(mLayout).draw(any(Canvas.class));
  }

  @Test
  public void testWarmGlyphForNullLayout() {
    mGlyphWarmerImpl.warmLayout(null);
    mShadowLooper.runOneTask();
  }
}
