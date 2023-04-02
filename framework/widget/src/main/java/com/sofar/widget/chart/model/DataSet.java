package com.sofar.widget.chart.model;

import java.util.ArrayList;
import java.util.List;

public class DataSet {

  public List<Entry> mEntries;
  private String mLabel = "DataSet";

  public DataSet(List<Entry> entries, String label) {
    this.mLabel = label;
    this.mEntries = entries;

    if (mEntries == null) {
      mEntries = new ArrayList<>();
    }
    calcMinMax();
  }

  public int getSize() {
    return mEntries.size();
  }

  private void calcMinMax() {}
}
