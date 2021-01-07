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

package com.facebook.fbui.textlayoutbuilder.util;

import static org.junit.Assert.assertEquals;

import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/** Tests {@link LayoutMeasureUtil} */
@Config(sdk = 21)
@RunWith(RobolectricTestRunner.class)
public class LayoutMeasureUtilTest {

  private static final String ONE_LINE_TEXT = "test";
  private static final String TWO_LINE_TEXT = "test\ntest";

  private Layout mLayout;

  @Test
  public void testOneLineWithAdd() {
    mLayout = StaticLayoutHelper.makeStaticLayout(ONE_LINE_TEXT, 1.0f, 5.0f);
    assertEquals(LayoutMeasureUtil.getHeight(mLayout), 10);
  }

  @Test
  public void testTwoLinesWithAdd() {
    mLayout = StaticLayoutHelper.makeStaticLayout(TWO_LINE_TEXT, 1.0f, 5.0f);
    assertEquals(LayoutMeasureUtil.getHeight(mLayout), 25);
  }

  @Test
  public void testOneLineWithMulti() {
    mLayout = StaticLayoutHelper.makeStaticLayout(ONE_LINE_TEXT, 1.5f, 0.0f);
    assertEquals(LayoutMeasureUtil.getHeight(mLayout), 10);
  }

  @Test
  public void testTwoLinesWithMulti() {
    mLayout = StaticLayoutHelper.makeStaticLayout(TWO_LINE_TEXT, 1.5f, 0.0f);
    assertEquals(LayoutMeasureUtil.getHeight(mLayout), 25);
  }

  @Test
  public void testOneLineWithAddAndMulti() {
    mLayout = StaticLayoutHelper.makeStaticLayout(ONE_LINE_TEXT, 1.5f, 2.0f);
    assertEquals(LayoutMeasureUtil.getHeight(mLayout), 10);
  }

  @Test
  public void testTwoLinesWithAddAndMulti() {
    mLayout = StaticLayoutHelper.makeStaticLayout(TWO_LINE_TEXT, 1.5f, 2.0f);
    assertEquals(LayoutMeasureUtil.getHeight(mLayout), 27);
  }

  @Test
  public void testEmptyTextWithAddAndMulti() {
    mLayout = StaticLayoutHelper.makeStaticLayout("", 1.5f, 2.0f);
    assertEquals(LayoutMeasureUtil.getHeight(mLayout), 10);
  }

  // TextPaint with a line height of 10.
  private static class DummyTextPaint extends TextPaint {
    @Override
    public int getFontMetricsInt(FontMetricsInt fmi) {
      if (fmi != null) {
        fmi.ascent = 0;
        fmi.top = 0;
        fmi.descent = 10;
        fmi.bottom = 10;
      }
      return 0;
    }
  }

  private static class StaticLayoutHelper {
    public static Layout makeStaticLayout(CharSequence text, float spacingMult, float spacingAdd) {
      return new StaticLayout(
          text,
          new DummyTextPaint(),
          1000,
          Layout.Alignment.ALIGN_NORMAL,
          spacingMult,
          spacingAdd,
          true);
    }
  }
}
