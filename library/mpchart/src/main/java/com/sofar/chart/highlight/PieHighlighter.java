package com.sofar.chart.highlight;

import com.sofar.chart.charts.PieChart;
import com.sofar.chart.data.Entry;
import com.sofar.chart.interfaces.datasets.IPieDataSet;

/**
 * Created by philipp on 12/06/16.
 */
public class PieHighlighter extends PieRadarHighlighter<PieChart> {

  public PieHighlighter(PieChart chart) {
    super(chart);
  }

  @Override
  protected Highlight getClosestHighlight(int index, float x, float y) {

    IPieDataSet set = mChart.getData().getDataSet();

    final Entry entry = set.getEntryForIndex(index);

    return new Highlight(index, entry.getY(), x, y, 0, set.getAxisDependency());
  }
}
