package com.sofar.widget.chart.model;

import java.util.List;

import android.graphics.Color;


public class LineDataSet extends DataSet {

  public LineDataSet.Mode mMode = Mode.CUBIC;
  public float mLineWidth = 2.5f;
  public int mLineColor = Color.BLACK;
  public float mCircleRadius = 8f;

  public LineDataSet(List<Entry> entries, String label) {
    super(entries, label);
  }

  public void setLineWidth(float width) {
    mLineWidth = width;
  }

  public void setLineColor(int color) {
    mLineColor = color;
  }

  public void setCircleRadius(float radius) {
    mCircleRadius = radius;
  }

  public enum Mode {
    LINEAR, CUBIC
  }
}
