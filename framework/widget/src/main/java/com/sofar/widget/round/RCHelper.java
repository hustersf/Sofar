package com.sofar.widget.round;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.sofar.widget.R;

public class RCHelper {

  public float[] radii = new float[8];   // top-left, top-right, bottom-right, bottom-left
  public Path mClipPath;                 // 剪裁区域路径
  public Paint mPaint;                   // 画笔
  public boolean mRoundAsCircle = false; // 圆形
  public RectF mLayer;                   // 画布图层大小

  public Path path;
  public RectF areas;

  public RCHelper() {
    mClipPath = new Path();

    mPaint = new Paint();
    mPaint.setColor(Color.WHITE);
    mPaint.setAntiAlias(true);
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
      mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    } else {
      mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
    }

    mLayer = new RectF();
    areas = new RectF();
    path = new Path();
  }

  public void initAttrs(Context context, AttributeSet attrs) {
    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RCAttrs);
    mRoundAsCircle = ta.getBoolean(R.styleable.RCAttrs_round_as_circle, false);
    int roundCorner = ta.getDimensionPixelSize(R.styleable.RCAttrs_round_corner, 0);
    int roundCornerTopLeft = ta.getDimensionPixelSize(
      R.styleable.RCAttrs_round_corner_top_left, roundCorner);
    int roundCornerTopRight = ta.getDimensionPixelSize(
      R.styleable.RCAttrs_round_corner_top_right, roundCorner);
    int roundCornerBottomLeft = ta.getDimensionPixelSize(
      R.styleable.RCAttrs_round_corner_bottom_left, roundCorner);
    int roundCornerBottomRight = ta.getDimensionPixelSize(
      R.styleable.RCAttrs_round_corner_bottom_right, roundCorner);
    ta.recycle();

    radii[0] = roundCornerTopLeft;
    radii[1] = roundCornerTopLeft;

    radii[2] = roundCornerTopRight;
    radii[3] = roundCornerTopRight;

    radii[4] = roundCornerBottomRight;
    radii[5] = roundCornerBottomRight;

    radii[6] = roundCornerBottomLeft;
    radii[7] = roundCornerBottomLeft;
  }

  public void onSizeChanged(View view, int w, int h) {
    mLayer.set(0, 0, w, h);
    refreshRegion(view);
  }

  private void refreshRegion(View view) {
    int w = (int) mLayer.width();
    int h = (int) mLayer.height();
    areas.left = view.getPaddingLeft();
    areas.top = view.getPaddingTop();
    areas.right = w - view.getPaddingRight();
    areas.bottom = h - view.getPaddingBottom();
    setPath();
  }

  public void setRoundLayoutRadius(float roundLayoutRadius) {
    radii[0] = roundLayoutRadius;
    radii[1] = roundLayoutRadius;

    radii[2] = roundLayoutRadius;
    radii[3] = roundLayoutRadius;

    radii[4] = roundLayoutRadius;
    radii[5] = roundLayoutRadius;

    radii[6] = roundLayoutRadius;
    radii[7] = roundLayoutRadius;

    setPath();
  }

  private void setPath() {
    mClipPath.reset();
    mClipPath.addRoundRect(areas, radii, Path.Direction.CW);

    path.reset();
    path.addRect(areas, Path.Direction.CW);
    path.op(mClipPath, Path.Op.DIFFERENCE);
  }

  public void onClipDraw(Canvas canvas) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
      canvas.drawPath(mClipPath, mPaint);
    } else {
      canvas.drawPath(path, mPaint);
    }
  }
}
