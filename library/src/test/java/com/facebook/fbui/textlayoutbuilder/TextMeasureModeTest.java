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

package com.facebook.fbui.textlayoutbuilder;

import static com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder.MEASURE_MODE_AT_MOST;
import static com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder.MEASURE_MODE_EXACTLY;
import static com.facebook.fbui.textlayoutbuilder.TextLayoutBuilder.MEASURE_MODE_UNSPECIFIED;
import static org.assertj.core.api.Assertions.assertThat;

import android.graphics.Typeface;
import android.text.Layout;
import com.facebook.fbui.textlayoutbuilder.shadows.ShadowLayout;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@Config(
    manifest = Config.NONE,
    shadows = {ShadowLayout.class})
@RunWith(RobolectricTestRunner.class)
public class TextMeasureModeTest {

  @Test
  public void testMeasureModeUnspecified() {
    final Layout layout =
        new TextLayoutBuilder()
            .setText(ShadowLayout.LONG_TEXT)
            .setWidth(20, MEASURE_MODE_UNSPECIFIED)
            .setTypeface(Typeface.DEFAULT)
            .setTextSize(10)
            .build();

    assertThat(layout.getWidth()).isEqualTo(ShadowLayout.LONG_TEXT_LENGTH);
  }

  @Test
  public void testMeasureModeExactly() {
    final Layout layout =
        new TextLayoutBuilder()
            .setText(ShadowLayout.LONG_TEXT)
            .setWidth(20, MEASURE_MODE_EXACTLY)
            .setTypeface(Typeface.DEFAULT)
            .setTextSize(10)
            .build();

    assertThat(layout.getWidth()).isEqualTo(20);
  }

  @Test
  public void testMeasureModeAtMostLongText() {
    final Layout layout =
        new TextLayoutBuilder()
            .setText(ShadowLayout.LONG_TEXT)
            .setWidth(20, MEASURE_MODE_AT_MOST)
            .setTypeface(Typeface.DEFAULT)
            .setTextSize(10)
            .build();

    assertThat(layout.getWidth()).isEqualTo(20);
  }

  @Test
  public void testMeasureModeAtMostShortText() {
    final Layout layout =
        new TextLayoutBuilder()
            .setText(ShadowLayout.SHORT_TEXT)
            .setWidth(20, MEASURE_MODE_AT_MOST)
            .setTypeface(Typeface.DEFAULT)
            .setTextSize(10)
            .build();

    assertThat(layout.getWidth()).isEqualTo(ShadowLayout.SHORT_TEXT_LENGTH);
  }

  @Test
  public void testLegacyBehaviour() {
    final Layout layout =
        new TextLayoutBuilder()
            .setText(ShadowLayout.LONG_TEXT)
            .setWidth(-1)
            .setTypeface(Typeface.DEFAULT)
            .setTextSize(10)
            .build();

    assertThat(layout.getWidth()).isEqualTo(ShadowLayout.LONG_TEXT_LENGTH);
  }
}
