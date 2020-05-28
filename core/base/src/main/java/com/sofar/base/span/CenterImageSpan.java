package com.sofar.base.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

/**
 * {@link android.text.style.DynamicDrawableSpan}
 */
public class CenterImageSpan extends ImageSpan {

  private WeakReference<Drawable> mDrawableRef;

  public CenterImageSpan(@NonNull Drawable drawable) {
    super(drawable);
  }

  @Override
  public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
    Drawable d = getCachedDrawable();
    Paint.FontMetricsInt fm = paint.getFontMetricsInt();
    int transY = y + (fm.descent + fm.ascent) / 2 - d.getBounds().bottom / 2;
    canvas.save();
    canvas.translate(x, transY);
    d.draw(canvas);
    canvas.restore();
  }

  private Drawable getCachedDrawable() {
    WeakReference<Drawable> wr = mDrawableRef;
    Drawable d = null;

    if (wr != null) {
      d = wr.get();
    }

    if (d == null) {
      d = getDrawable();
      mDrawableRef = new WeakReference<Drawable>(d);
    }

    return d;
  }
}
