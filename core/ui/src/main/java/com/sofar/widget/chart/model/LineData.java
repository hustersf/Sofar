package com.sofar.widget.chart.model;


import java.util.List;

public class LineData {

  public List<LineDataSet> mDataSets;

  public LineData(List<LineDataSet> sets) {
    this.mDataSets = sets;
    notifyDataChanged();
  }

  private void notifyDataChanged() {
    calcMinMax();
  }

  private void calcMinMax() {

  }
}
