package com.sofar.chart.interfaces.dataprovider;

import com.sofar.chart.data.BubbleData;

public interface BubbleDataProvider extends BarLineScatterCandleBubbleDataProvider {

  BubbleData getBubbleData();
}
