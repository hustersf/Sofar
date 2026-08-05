
package com.sofar.chart.data;

import java.util.List;

import com.sofar.chart.interfaces.datasets.ILineDataSet;

/**
 * Data object that encapsulates all data associated with a LineChart.
 *
 * @author Philipp Jahoda
 */
public class LineData extends BarLineScatterCandleBubbleData<ILineDataSet> {

  public LineData() {
    super();
  }

  public LineData(ILineDataSet... dataSets) {
    super(dataSets);
  }

  public LineData(List<ILineDataSet> dataSets) {
    super(dataSets);
  }
}
