package com.sofar.widget.round;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class RCRelativeLayout extends RelativeLayout {

  RCHelper rcHelper = new RCHelper();

  public RCRelativeLayout(Context context) {
    this(context, null);
  }

  public RCRelativeLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public RCRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    rcHelper.initAttrs(context, attrs);
  }


  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    rcHelper.onSizeChanged(this, w, h);
  }

  @Override
  protected void dispatchDraw(Canvas canvas) {
    canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
    super.dispatchDraw(canvas);
    rcHelper.onClipDraw(canvas);
    canvas.restore();
  }
}
