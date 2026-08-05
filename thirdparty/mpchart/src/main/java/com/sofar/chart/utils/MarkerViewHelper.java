package com.sofar.chart.utils;

import android.animation.ObjectAnimator;
import android.view.View;

import com.sofar.chart.charts.Chart;
import com.sofar.chart.data.Entry;
import com.sofar.chart.highlight.Highlight;

/**
 * 不通用，但可以留作参考
 */
public class MarkerViewHelper {

  private Chart chart;
  private View markerView;

  public MarkerViewHelper(Chart chart, View markerView) {
    this.chart = chart;
    this.markerView = markerView;
  }

  public void moveWithHighlight(Entry e, Highlight highlight) {
    float w1 = markerView.getWidth();
    float h1 = markerView.getHeight();
    float w2 = chart.getWidth();
    float h2 = chart.getHeight();

    float x = highlight.getXPx();

    float curX = markerView.getTranslationX();
    float targetX = x - w1 / 2;
    if (targetX <= 0) {
      targetX = 0;
    } else if (targetX > w2 - w1) {
      targetX = w2 - w1;
    }
    ObjectAnimator anim = ObjectAnimator.ofFloat(markerView, "translationX", curX, targetX);
    anim.setDuration(0);
    anim.start();
  }
}
