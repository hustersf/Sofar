package com.sofar.widget.chart.component;

import android.graphics.Color;
import android.graphics.DashPathEffect;

public class AxisBase {

  public float mAxisMaximum = 0f;
  public float mAxisMinimum = 0f;
  public float mAxisRange = 0f;

  public int mLabelCount = 6;

  public int mAxisLineColor = Color.GRAY;
  public float mAxisLineWidth = 6f;
  public DashPathEffect mAxisLineDashPathEffect = null;

  public int mGridColor = Color.GRAY;
  public float mGridLineWidth = 3f;
  public DashPathEffect mGridDashPathEffect = null;

  public int mAxisLabelTextSize = 12;
  public float mItemWidth = 50f;

  public void setAxisMaximum(float max) {
    mAxisMaximum = max;
    this.mAxisRange = Math.abs(max - mAxisMinimum);
  }

  public void setAxisMinimum(float min) {
    mAxisMinimum = min;
    this.mAxisRange = Math.abs(mAxisMaximum - min);
  }

  public void setLabelCount(int count) {
    mLabelCount = count;
  }

  public void setAxisLineColor(int color) {
    mAxisLineColor = color;
  }

  public void setAxisLineWidth(float width) {
    mAxisLineWidth = width;
  }

  public void enableAxisLineDashedLine(float lineLength, float spaceLength, float phase) {
    mAxisLineDashPathEffect = new DashPathEffect(new float[]{lineLength, spaceLength}, phase);
  }

  public void enableGridDashedLine(float lineLength, float spaceLength, float phase) {
    mGridDashPathEffect = new DashPathEffect(new float[]{lineLength, spaceLength}, phase);
  }

}
