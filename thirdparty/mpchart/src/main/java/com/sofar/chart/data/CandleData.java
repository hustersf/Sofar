package com.sofar.chart.data;

import java.util.List;

import com.sofar.chart.interfaces.datasets.ICandleDataSet;

public class CandleData extends BarLineScatterCandleBubbleData<ICandleDataSet> {

  public CandleData() {
    super();
  }

  public CandleData(List<ICandleDataSet> dataSets) {
    super(dataSets);
  }

  public CandleData(ICandleDataSet... dataSets) {
    super(dataSets);
  }
}
