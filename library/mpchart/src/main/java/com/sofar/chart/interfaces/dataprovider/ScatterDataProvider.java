package com.sofar.chart.interfaces.dataprovider;

import com.sofar.chart.data.ScatterData;

public interface ScatterDataProvider extends BarLineScatterCandleBubbleDataProvider {

  ScatterData getScatterData();
}
