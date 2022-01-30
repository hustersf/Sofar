package com.sofar.widget.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.sofar.widget.R;

public class LineProgress extends ProgressBar {

  Paint paint;
  int reachColor = 0xFFFF5800;
  int unReachColor = 0xFFFFFFFF;
  boolean round;

  public LineProgress(Context context) {
    this(context, null);
  }

  public LineProgress(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public LineProgress(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LineProgress);
    reachColor = ta.getColor(R.styleable.LineProgress_reachColor, reachColor);
    unReachColor = ta.getColor(R.styleable.LineProgress_unReachColor, unReachColor);
    round = ta.getBoolean(R.styleable.LineProgress_sideRound, round);
    ta.recycle();

    init();
  }

  private void init() {
    paint = new Paint();
    paint.setAntiAlias(true); // 抗锯齿
    paint.setDither(true); // 抗抖动
    paint.setStyle(Paint.Style.FILL);
  }

  @Override
  protected synchronized void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    int width = getWidth() - getPaddingLeft() - getPaddingRight();
    int height = getHeight() - getPaddingTop() - getPaddingBottom();
    canvas.save();
    canvas.translate(getPaddingLeft(), getHeight() / 2);
    int strokeWidth = height;
    float rate = getProgress() * 1.0f / getMax();
    int progressX = (int) (rate * width);
    if (progressX >= width - strokeWidth / 2) {
      progressX = width - strokeWidth / 2;
    }
    if (progressX <= strokeWidth / 2) {
      progressX = strokeWidth / 2;
    }

    paint.setStrokeWidth(strokeWidth);
    if (round) {
      paint.setStrokeCap(Paint.Cap.ROUND);
    } else {
      paint.setStrokeCap(Paint.Cap.SQUARE);
    }
    paint.setColor(unReachColor);
    canvas.drawLine(progressX, 0, width - strokeWidth / 2, 0, paint);
    paint.setColor(reachColor);
    if (rate > 0) {
      canvas.drawLine(strokeWidth / 2, 0, progressX, 0, paint);
    }

    canvas.restore();
  }

  /**
   * 更改已完成进度的颜色
   */
  public void setReachColor(int color) {
    this.reachColor = color;
    invalidate();
  }

  /**
   * 更改未完成进度的颜色
   */
  public void setUnReachColor(int color) {
    this.unReachColor = color;
    invalidate();
  }

}
