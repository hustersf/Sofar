package com.sofar.chart.interfaces.dataprovider;

import com.sofar.chart.components.YAxis.AxisDependency;
import com.sofar.chart.data.BarLineScatterCandleBubbleData;
import com.sofar.chart.utils.Transformer;

public interface BarLineScatterCandleBubbleDataProvider extends ChartInterface {

  Transformer getTransformer(AxisDependency axis);

  boolean isInverted(AxisDependency axis);

  float getLowestVisibleX();

  float getHighestVisibleX();

  BarLineScatterCandleBubbleData getData();
}
