package com.sofar.widget.textview.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ReplacementSpan;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 一个【背景+文字】的标签富文本
 */
public class RoundBackgroundSpan extends ReplacementSpan {

  @NonNull
  Paint mPaint;

  @NonNull
  RectF mRectF;

  int mSize;
  int mSpanHeight;
  int mBgColor;
  int mTextColor;
  int mRadiusPx;
  int mRightMarginPx;
  int mTextLeftPadding;
  int mTextRightPadding;
  int mStrokeWidth;

  public RoundBackgroundSpan() {
    mPaint = new Paint();
    mPaint.setAntiAlias(true); // 抗锯齿
    mPaint.setDither(true); // 抗抖动
    mPaint.setStyle(Paint.Style.FILL);
    mPaint.setTextAlign(Paint.Align.CENTER);
    mRectF = new RectF();
  }

  public void setTextSize(int textSizePx) {
    mPaint.setTextSize(textSizePx);
  }

  public void setSpanHeight(int spanHeight) {
    this.mSpanHeight = spanHeight;
  }

  public void setTextColor(int textColor) {
    this.mTextColor = textColor;
  }

  public void setBgColor(int bgColor) {
    this.mBgColor = bgColor;
  }

  public void setRadiusPx(int radiusPx) {
    this.mRadiusPx = radiusPx;
  }

  public void setRightMarginPx(int rightMarginPx) {
    this.mRightMarginPx = rightMarginPx;
  }

  public void setTextPadding(int textLeftPadding, int textRightPadding) {
    this.mTextLeftPadding = textLeftPadding;
    this.mTextRightPadding = textRightPadding;
  }

  public void setBold(boolean bold) {
    mPaint.setFakeBoldText(bold);
  }

  public void setStroke(int width) {
    this.mStrokeWidth = width;
  }

  /**
   * 返回span的宽度
   */
  @Override
  public int getSize(@NonNull Paint paint, CharSequence text, int start, int end,
    @Nullable Paint.FontMetricsInt fm) {
    mSize = (int) mPaint.measureText(text, start, end) + mRightMarginPx + mTextLeftPadding +
      mTextRightPadding;
    return mSize;
  }

  /**
   * 绘制
   */
  @Override
  public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top,
    int y, int bottom, @NonNull Paint paint) {
    drawBgRect(canvas, x, top, y, bottom, paint);
    drawText(canvas, text, start, end);
  }


  private void drawBgRect(@NonNull Canvas canvas, float x, int top, int y, int bottom,
    @NonNull Paint paint) {
    Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
    mRectF.left = x;
    mRectF.right = x + mSize - mRightMarginPx;
    if (mSpanHeight > 0) {
      mRectF.top = (bottom - top - mSpanHeight) / 2;
      mRectF.bottom = (bottom - top + mSpanHeight) / 2;
    } else {
      mRectF.top = y + fontMetrics.ascent;
      mRectF.bottom = y + fontMetrics.descent;
    }
    if (mStrokeWidth > 0) {
      mPaint.setStyle(Paint.Style.STROKE);
    } else {
      mPaint.setStyle(Paint.Style.FILL);
    }
    mPaint.setColor(mBgColor);
    canvas.drawRoundRect(mRectF, mRadiusPx, mRadiusPx, mPaint);
  }

  private void drawText(@NonNull Canvas canvas, CharSequence text, int start, int end) {
    final String tag = text.subSequence(start, end).toString();
    mPaint.setStyle(Paint.Style.FILL);
    mPaint.setColor(mTextColor);

    Paint.FontMetricsInt tagFontMetrics = mPaint.getFontMetricsInt();
    float textCenterX = (mRectF.right - mRectF.left) / 2 + mRectF.left;
    float textHeight = tagFontMetrics.descent - tagFontMetrics.ascent;
    float rectCenterY = mRectF.bottom - (mRectF.bottom - mRectF.top) / 2;
    float tagBaseLineY = rectCenterY + textHeight / 2 - tagFontMetrics.descent;
    canvas.drawText(tag, textCenterX, tagBaseLineY, mPaint);
  }
}
