package com.sofar.widget.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class ResultView extends FrameLayout implements ResultStatus {


  public ResultView(@NonNull Context context) {
    this(context, null);
  }

  public ResultView(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ResultView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }


  @Override
  public boolean isAvailable() {
    return true;
  }

  @Override
  public void onPrepare() {
  }

  @Override
  public void onShow() {
  }

  @Override
  public void onHide() {
  }


  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
  }
}
