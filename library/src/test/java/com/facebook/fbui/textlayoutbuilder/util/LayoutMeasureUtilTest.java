/**
 * Copyright (c) 2016-present, Facebook, Inc. All rights reserved.
 *
 * <p>This source code is licensed under the BSD-style license found in the LICENSE file in the root
 * directory of this source tree. An additional grant of patent rights can be found in the PATENTS
 * file in the same directory.
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
@Config(manifest = Config.NONE)
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
