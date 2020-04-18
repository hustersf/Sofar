package com.sofar.widget.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.sofar.widget.R;

/**
 * 仿百度投票
 */
public class VoteProgress extends ProgressBar {

  private int leftColor = 0xFFE56056;
  private int rightColor = 0xFF51971A;
  private int spaceColor = 0xFFFFFFFF;

  private int strokeWidth;
  private Paint paint;
  private Path path;
  private float minRate = 0.1f;
  private float spaceWidth;

  public VoteProgress(Context context) {
    this(context, null);
  }

  public VoteProgress(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public VoteProgress(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.VoteProgress);
    leftColor = ta.getColor(R.styleable.VoteProgress_leftColor, leftColor);
    rightColor = ta.getColor(R.styleable.VoteProgress_rightColor, rightColor);
    spaceColor = ta.getColor(R.styleable.VoteProgress_spaceColor, spaceColor);
    spaceWidth = ta.getDimension(R.styleable.VoteProgress_spaceWidth, spaceWidth);

    init();
  }

  private void init() {
    paint = new Paint();
    paint.setAntiAlias(true); // 抗锯齿
    paint.setDither(true); // 抗抖动
    // paint.setStrokeCap(Paint.Cap.ROUND); // 边缘为圆
    paint.setStyle(Paint.Style.FILL);

    path = new Path();
  }

  @Override
  protected synchronized void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    int width = getWidth() - getPaddingLeft() - getPaddingRight();
    int height = getHeight() - getPaddingTop() - getPaddingBottom();
    canvas.save();

    canvas.translate(getPaddingLeft(), getHeight() / 2);
    //绘制左右进度线
    if (strokeWidth <= 0) {
      strokeWidth = height;
    }
    paint.setStrokeWidth(strokeWidth);
    float rate = getProgress() * 1.0f / getMax();
    if (rate < minRate) {
      rate = minRate;
    }
    if (rate > 1 - minRate) {
      rate = 1 - minRate;
    }
    int progressX = (int) (rate * width);
    if (progressX >= width - strokeWidth) {
      progressX = width - strokeWidth;
    }
    paint.setColor(leftColor);
    canvas.drawLine(strokeWidth / 2, 0, progressX, 0, paint);
    canvas.drawCircle(strokeWidth / 2, 0, strokeWidth / 2, paint);
    paint.setColor(rightColor);
    canvas.drawLine(progressX, 0, width - strokeWidth / 2, 0, paint);
    canvas.drawCircle(width - strokeWidth / 2, 0, strokeWidth / 2, paint);

    //绘制中间间隔线
    paint.setColor(spaceColor);
    float xOffset = spaceWidth;
    float yOffset = height;
    path.moveTo(progressX, yOffset / 2);
    path.lineTo(progressX + xOffset, -yOffset / 2);
    path.lineTo(progressX, -yOffset / 2);
    path.lineTo(progressX - xOffset, yOffset / 2);
    path.close();
    canvas.drawPath(path, paint);

    canvas.restore();
  }

  /**
   * 决定进度条的高度 px
   */
  public void setHeight(int height) {
    this.strokeWidth = height;
  }

  /**
   * 设置某一方的最小占比
   */
  public void setMinProgress(float rate) {
    minRate = rate;
  }

}
