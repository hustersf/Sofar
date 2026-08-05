package com.sofar.widget.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.sofar.utility.DeviceUtil;

public class CustomRefreshLayout extends RefreshLayout {

  private RefreshView refreshView;

  public CustomRefreshLayout(Context context) {
    super(context);
  }

  public CustomRefreshLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected View onCreateRefreshView() {
    refreshView = new RefreshView(getContext());
    if (refreshView.getPaddingTop() == 0 && refreshView.getPaddingBottom() == 0) {
      int padding = DeviceUtil.dp2px(getContext(), 15);
      refreshView.setPadding(padding, padding, padding, padding);
    }
    setRefreshViewSize(ViewGroup.LayoutParams.WRAP_CONTENT);
    return refreshView;
  }

  @Override
  protected DragDistanceConverter onCreateDragDistanceConvert() {
    return new CustomDragDistanceConvert(DeviceUtil.getMetricsHeight(getContext()));
  }

}
