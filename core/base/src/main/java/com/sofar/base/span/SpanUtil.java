package com.sofar.base.span;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;

public class SpanUtil {

  public static SpannableString getLeftImageSpan(String text, Drawable drawable, int drawablePadding) {
    text = "12" + text;
    SpannableString spannableString = new SpannableString(text);
    CenterImageSpan imageSpan = new CenterImageSpan(drawable);
    spannableString.setSpan(imageSpan, 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

    Drawable placeDrawable = new ColorDrawable();
    CenterImageSpan span = new CenterImageSpan(placeDrawable);
    placeDrawable.setBounds(0, 0, drawablePadding, 0);
    spannableString.setSpan(span, 1, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
    return spannableString;
  }
}
