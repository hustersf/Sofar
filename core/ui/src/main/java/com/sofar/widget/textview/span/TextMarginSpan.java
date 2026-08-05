package com.sofar.widget.textview.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

/**
 * 在文字的前line行,预留一个间距margin
 * 类似写文章时，第一行空2格的效果
 */
public class TextMarginSpan implements LeadingMarginSpan.LeadingMarginSpan2 {

  private int line;
  private int margin;

  public TextMarginSpan(int line, int margin) {
    this.line = line;
    this.margin = margin;
  }

  @Override
  public int getLeadingMarginLineCount() {
    return line;
  }

  @Override
  public int getLeadingMargin(boolean first) {
    if (first) {
      return margin;
    }
    return 0;
  }

  @Override
  public void drawLeadingMargin(Canvas c, Paint p, int x, int dir, int top, int baseline,
    int bottom, CharSequence text, int start, int end, boolean first, Layout layout) {
  }
}
