package com.sofar.widget.line;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

import com.sofar.widget.R;

public class DashLineView extends View {
  static public int ORIENTATION_HORIZONTAL = 0;
  static public int ORIENTATION_VERTICAL = 1;

  private int lineColor = 0xFFFF5800;
  private float dashWidth = 0;
  private float dashGap = 0;
  private int orientation;

  private Paint paint;
  private Path path;

  public DashLineView(Context context) {
    this(context, null);
  }

  public DashLineView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public DashLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DashLineView);
    lineColor = ta.getColor(R.styleable.DashLineView_lineColor, lineColor);
    dashWidth = ta.getDimension(R.styleable.DashLineView_dashWidth, dashWidth);
    dashGap = ta.getDimension(R.styleable.DashLineView_dashGap, dashGap);
    orientation = ta.getInt(R.styleable.DashLineView_dashOrientation, ORIENTATION_HORIZONTAL);
    ta.recycle();

    paint = new Paint();
    paint.setAntiAlias(true);
    paint.setColor(lineColor);
    paint.setStyle(Paint.Style.STROKE);
    paint.setPathEffect(new DashPathEffect(new float[]{dashWidth, dashGap}, 0));
    path = new Path();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    int width = getWidth();
    int height = getHeight();
    path.reset();
    if (orientation == ORIENTATION_HORIZONTAL) {
      paint.setStrokeWidth(height);
      path.moveTo(0, height / 2);
      path.lineTo(width, height / 2);
    } else {
      paint.setStrokeWidth(width);
      path.moveTo(width / 2, 0);
      path.lineTo(width / 2, height);
    }
    canvas.drawPath(path, paint);
  }
}
