package com.sofar.widget.chart.component;

public class YAxis extends AxisBase {

  public YAxisPosition mPosition = YAxisPosition.LEFT;

  public void setPosition(YAxisPosition position) {
    mPosition = position;
  }

  public enum YAxisPosition {
    LEFT, RIGHT
  }
}
