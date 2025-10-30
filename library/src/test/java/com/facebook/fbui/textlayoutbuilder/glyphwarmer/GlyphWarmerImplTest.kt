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

import android.text.Layout
import com.facebook.fbui.textlayoutbuilder.shadows.ShadowPicture
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadow.api.Shadow
import org.robolectric.shadows.ShadowLooper

/** Tests [GlyphWarmerImpl]. */
@Config(shadows = [ShadowPicture::class])
@RunWith(RobolectricTestRunner::class)
class GlyphWarmerImplTest {

  @Mock lateinit var layout: Layout

  private lateinit var shadowLooper: ShadowLooper
  private lateinit var glyphWarmerImpl: GlyphWarmerImpl

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)

    glyphWarmerImpl = GlyphWarmerImpl()
    shadowLooper = Shadow.extract(glyphWarmerImpl.getWarmHandlerLooper()) as ShadowLooper
  }

  @Test
  fun testWarmGlyph() {
    glyphWarmerImpl.warmLayout(layout)
    shadowLooper.runOneTask()
    verify(layout).draw(any())
  }
}
