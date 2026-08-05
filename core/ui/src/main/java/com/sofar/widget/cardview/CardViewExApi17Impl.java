package com.sofar.widget.cardview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class CardViewExApi17Impl extends CardViewExBaseImpl {

  @Override
  public void initStatic() {
    RoundRectDrawableWithShadow.sRoundRectHelper =
      new RoundRectDrawableWithShadow.RoundRectHelper() {
        @Override
        public void drawRoundRect(Canvas canvas, RectF bounds, float cornerRadius,
          Paint paint) {
          canvas.drawRoundRect(bounds, cornerRadius, cornerRadius, paint);
        }
      };
  }
}
