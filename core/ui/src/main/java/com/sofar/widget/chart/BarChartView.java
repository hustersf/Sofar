package com.sofar.widget.chart;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

/**
 * 柱状图
 */
public class BarChartView extends View {

  public BarChartView(Context context) {
    this(context, null);
  }

  public BarChartView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public BarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {

  }
}
