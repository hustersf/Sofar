package com.sofar.widget.textview.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.style.ReplacementSpan;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 垂直居中+颜色效果
 */
public class VerticalCenterTextSizeSpan extends ReplacementSpan {

  private int fontSize;   //字体大小px
  private int color;  //字体颜色

  public VerticalCenterTextSizeSpan(int fontSize) {
    this.fontSize = fontSize;
  }

  public void setColor(int color) {
    this.color = color;
  }

  @Override
  public int getSize(@NonNull Paint paint, CharSequence text, int start, int end,
    @Nullable Paint.FontMetricsInt fm) {
    text = text.subSequence(start, end);
    Paint p = getTextPaint(paint);
    return (int) p.measureText(text.toString());
  }

  @Override
  public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top,
    int y, int bottom, @NonNull Paint paint) {
    text = text.subSequence(start, end);
    Paint p = getTextPaint(paint);
    Paint.FontMetricsInt fm = p.getFontMetricsInt();
    canvas
      .drawText(text.toString(), x, y - ((y + fm.descent + y + fm.ascent) / 2 - (bottom + top) / 2),
        p);
  }

  private TextPaint getTextPaint(Paint srcPaint) {
    TextPaint paint = new TextPaint(srcPaint);
    paint.setTextSize(fontSize);
    if (color != 0) {
      paint.setColor(color);
    }
    return paint;
  }

}
