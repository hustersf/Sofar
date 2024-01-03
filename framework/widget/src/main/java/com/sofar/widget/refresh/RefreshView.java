package com.sofar.widget.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class RefreshView extends RelativeLayout implements RefreshStatus {


  public RefreshView(Context context) {
    this(context, null);
  }

  public RefreshView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public RefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  private void updateColor() {
  }

  @Override
  public void reset() {
  }

  @Override
  public void refreshing() {

  }

  @Override
  public void refreshComplete() {}

  @Override
  public void pullToRefresh() {}

  @Override
  public void releaseToRefresh() {}

  @Override
  public void pullProgress(float pullDistance, float pullProgress) {
  }

}
