package com.sofar.chart.interfaces.dataprovider;

import com.sofar.chart.components.YAxis;
import com.sofar.chart.data.LineData;

public interface LineDataProvider extends BarLineScatterCandleBubbleDataProvider {

  LineData getLineData();

  YAxis getAxis(YAxis.AxisDependency dependency);
}
