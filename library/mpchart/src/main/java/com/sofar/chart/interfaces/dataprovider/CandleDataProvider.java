package com.sofar.chart.interfaces.dataprovider;

import com.sofar.chart.data.CandleData;

public interface CandleDataProvider extends BarLineScatterCandleBubbleDataProvider {

  CandleData getCandleData();
}
