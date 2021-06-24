package com.sofar.widget.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    int width = getWidth();
    int height = getHeight();
    int offset = (int) mBoxShadowBlur;
    mRectF.set(offset, offset, width - offset, height - offset);
    canvas.save();
    canvas.translate(mBoxShadowDx, mBoxShadowDy);
    canvas.drawRoundRect(mRectF, mBoxShadowRadius, mBoxShadowRadius, mBoxShadowPaint);
    if (mBoxOverlayColor != Color.TRANSPARENT) {
      canvas.drawRoundRect(mRectF, mBoxShadowRadius, mBoxShadowRadius, mBoxOverlayPaint);
    }
    canvas.restore();
    super.dispatchDraw(canvas);
  }

}
