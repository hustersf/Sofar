package com.sofar.widget.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sofar.widget.R;

/**
 * 替代 android:elevation
 * 可自定义颜色
 */
public class BoxShadowLayout extends FrameLayout {

  private RectF mRectF = new RectF();
  private float mBoxShadowBlur;
  private int mBoxShadowColor;
  private float mBoxShadowRadius;
  private float mBoxShadowSpread;
  private float mBoxShadowDx;
  private float mBoxShadowDy;
  private Paint mBoxShadowPaint;
  private int mBoxOverlayColor;
  private Paint mBoxOverlayPaint;

  public BoxShadowLayout(@NonNull Context context) {
    this(context, null);
  }

  public BoxShadowLayout(@NonNull Context context,
    @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public BoxShadowLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }


  private void init(Context context, AttributeSet attrs) {
    TypedArray box = context.obtainStyledAttributes(attrs, R.styleable.BoxShadowLayout, 0, 0);
    try {
      mBoxShadowColor = box.getColor(R.styleable.BoxShadowLayout_box_shadowColor, 0);
      mBoxShadowRadius = box.getDimension(R.styleable.BoxShadowLayout_box_shadowRadius, 0f);
      mBoxShadowBlur = box.getDimension(R.styleable.BoxShadowLayout_box_shadowBlur, 0f);
      mBoxShadowSpread = box.getDimension(R.styleable.BoxShadowLayout_box_shadowSpread, 0f);
      mBoxShadowDx = box.getDimension(R.styleable.BoxShadowLayout_box_shadowDx, 0f);
      mBoxShadowDy = box.getDimension(R.styleable.BoxShadowLayout_box_shadowDy, 0f);
      mBoxOverlayColor = box.getColor(R.styleable.BoxShadowLayout_box_overlayColor, 0);
    } finally {
      box.recycle();
    }

    int padding = (int) mBoxShadowBlur;
    setPadding(padding, padding, padding, padding);

    mBoxShadowPaint = new Paint();
    mBoxShadowPaint.setAntiAlias(true);
    mBoxShadowPaint.setColor(mBoxShadowColor);
    mBoxShadowPaint.setStyle(Paint.Style.FILL);
    mBoxShadowPaint.setMaskFilter(new BlurMaskFilter(mBoxShadowBlur, BlurMaskFilter.Blur.OUTER));

    mBoxOverlayPaint = new Paint();
    mBoxOverlayPaint.setAntiAlias(true);
    mBoxOverlayPaint.setColor(mBoxOverlayColor);
    mBoxOverlayPaint.setStyle(Paint.Style.FILL);
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    int paddingLeft = getPaddingLeft();
    int paddingRight = getPaddingRight();
    int paddingTop = getPaddingTop();
    int paddingBottom = getPaddingBottom();
    int width = getWidth();
    int height = getHeight();
    Path outline = getRoundCornerPath(paddingLeft, paddingTop, width - paddingLeft - paddingRight,
      height - paddingTop - paddingBottom, mBoxShadowRadius, mBoxShadowRadius, mBoxShadowRadius,
      mBoxShadowRadius);
    outline.offset(mBoxShadowDx, mBoxShadowDy);
    canvas.drawPath(outline, mBoxShadowPaint);
    if (mBoxOverlayColor != Color.TRANSPARENT) {
      canvas.drawPath(outline, mBoxOverlayPaint);
    }
    super.dispatchDraw(canvas);
  }

  private Path getRoundCornerPath(float dx, float dy, float width, float height,
    float topLeftRadius, float topRightRadius, float bottomRightRadius, float bottomLeftRadius) {
    Path path = new Path();
    path.moveTo(0f, topLeftRadius);
    if (topLeftRadius > 0) {
      path.arcTo(
        new RectF(0f, 0f, topLeftRadius * 2f, topLeftRadius * 2f), -180f, 90f);
    }
    path.lineTo(width - topRightRadius, 0f);
    if (topRightRadius > 0) {
      path.arcTo(
        new RectF((width - 2f * topRightRadius), 0f, width, topRightRadius * 2f), -90f, 90f);
    }
    path.lineTo(width, height - bottomRightRadius);
    if (bottomRightRadius > 0) {
      path.arcTo(
        new RectF((width - 2 * bottomRightRadius), (height - 2 * bottomRightRadius), width, height),
        0f, 90f);
    }
    path.lineTo(bottomLeftRadius, height);
    if (bottomLeftRadius > 0) {
      path.arcTo(
        new RectF(0f, height - 2f * bottomLeftRadius, bottomLeftRadius * 2f, height), 90f, 90f);
    }
    path.offset(dx, dy);
    path.close();
    return path;
  }

}
