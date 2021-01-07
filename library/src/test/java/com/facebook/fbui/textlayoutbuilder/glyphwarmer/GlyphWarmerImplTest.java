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
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowLooper;

/** Tests {@link GlyphWarmerImpl}. */
@Config(shadows = {ShadowPicture.class})
@RunWith(RobolectricTestRunner.class)
public class GlyphWarmerImplTest {

  @Mock Layout mLayout;

  private ShadowLooper mShadowLooper;
  private GlyphWarmerImpl mGlyphWarmerImpl;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);

    mGlyphWarmerImpl = new GlyphWarmerImpl();
    mShadowLooper = (ShadowLooper) Shadow.extract(mGlyphWarmerImpl.getWarmHandlerLooper());
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
