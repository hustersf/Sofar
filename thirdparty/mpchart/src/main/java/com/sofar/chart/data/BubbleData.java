
package com.sofar.chart.data;

import java.util.List;

import com.sofar.chart.interfaces.datasets.IBubbleDataSet;

public class BubbleData extends BarLineScatterCandleBubbleData<IBubbleDataSet> {

  public BubbleData() {
    super();
  }

  public BubbleData(IBubbleDataSet... dataSets) {
    super(dataSets);
  }

  public BubbleData(List<IBubbleDataSet> dataSets) {
    super(dataSets);
  }


  /**
   * Sets the width of the circle that surrounds the bubble when highlighted
   * for all DataSet objects this data object contains, in dp.
   *
   * @param width
   */
  public void setHighlightCircleWidth(float width) {
    for (IBubbleDataSet set : mDataSets) {
      set.setHighlightCircleWidth(width);
    }
  }
}
