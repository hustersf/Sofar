package com.sofar.chart.interfaces.dataprovider;

import com.sofar.chart.data.BarData;

public interface BarDataProvider extends BarLineScatterCandleBubbleDataProvider {

  BarData getBarData();

  boolean isDrawBarShadowEnabled();

  boolean isDrawValueAboveBarEnabled();

  boolean isHighlightFullBarEnabled();
}
