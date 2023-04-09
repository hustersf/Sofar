
package com.sofar.chart.charts;

import android.content.Context;
import android.util.AttributeSet;

import com.sofar.chart.data.LineData;
import com.sofar.chart.interfaces.dataprovider.LineDataProvider;
import com.sofar.chart.renderer.LineChartRenderer;

/**
 * Chart that draws lines, surfaces, circles, ...
 *
 * @author Philipp Jahoda
 */
public class LineChart extends BarLineChartBase<LineData> implements LineDataProvider {

  public LineChart(Context context) {
    super(context);
  }

  public LineChart(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public LineChart(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void init() {
    super.init();

    mRenderer = new LineChartRenderer(this, mAnimator, mViewPortHandler);
  }

  @Override
  public LineData getLineData() {
    return mData;
  }

  @Override
  protected void onDetachedFromWindow() {
    // releases the bitmap in the renderer to avoid oom error
    if (mRenderer != null && mRenderer instanceof LineChartRenderer) {
      ((LineChartRenderer) mRenderer).releaseBitmap();
    }
    super.onDetachedFromWindow();
  }
}
