
package com.sofar.chart.charts;

import android.content.Context;
import android.util.AttributeSet;

import com.sofar.chart.data.CandleData;
import com.sofar.chart.interfaces.dataprovider.CandleDataProvider;
import com.sofar.chart.renderer.CandleStickChartRenderer;

/**
 * Financial chart type that draws candle-sticks (OHCL chart).
 *
 * @author Philipp Jahoda
 */
public class CandleStickChart extends BarLineChartBase<CandleData> implements CandleDataProvider {

  public CandleStickChart(Context context) {
    super(context);
  }

  public CandleStickChart(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public CandleStickChart(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void init() {
    super.init();

    mRenderer = new CandleStickChartRenderer(this, mAnimator, mViewPortHandler);

    getXAxis().setSpaceMin(0.5f);
    getXAxis().setSpaceMax(0.5f);
  }

  @Override
  public CandleData getCandleData() {
    return mData;
  }
}
