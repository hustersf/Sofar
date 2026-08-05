package com.sofar.widget.textview.span;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class ProtocolSpan extends ClickableSpan {

  private boolean mUnderline;
  private int mLinkColor;
  private boolean mBold;
  private OnProtocolClickListener mListener;

  public ProtocolSpan() {
    mLinkColor = 0xFFFF5800;
  }

  public void setUnderlineText(boolean underline) {
    mUnderline = underline;
  }

  public void setFakeBoldText(boolean bold) {
    mBold = bold;
  }

  public void setLinkColor(int color) {
    mLinkColor = color;
  }

  @Override
  public void updateDrawState(TextPaint ds) {
    ds.linkColor = mLinkColor;
    ds.setFakeBoldText(mBold);
    super.updateDrawState(ds);
    ds.setUnderlineText(mUnderline);
  }

  @Override
  public void onClick(View widget) {
    if (mListener != null) {
      mListener.onClick();
    }
  }

  public void setOnProtocolClickListener(OnProtocolClickListener listener) {
    mListener = listener;
  }

  public interface OnProtocolClickListener {
    void onClick();
  }
}
