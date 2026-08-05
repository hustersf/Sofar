package com.sofar.chart.interfaces.dataprovider;

import com.sofar.chart.data.CombinedData;

/**
 * Created by philipp on 11/06/16.
 */
public interface CombinedDataProvider
  extends LineDataProvider, BarDataProvider, BubbleDataProvider, CandleDataProvider,
  ScatterDataProvider {

  CombinedData getCombinedData();
}
