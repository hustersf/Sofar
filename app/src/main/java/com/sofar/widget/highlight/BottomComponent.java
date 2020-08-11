package com.sofar.widget.highlight;

import android.view.LayoutInflater;
import android.view.View;

import com.sofar.R;

public class BottomComponent implements Component {

  @Override
  public View getView(LayoutInflater inflater) {
    return inflater.inflate(R.layout.highlight_bottom_guide, null);
  }

  @Override
  public int getAnchor() {
    return ANCHOR_BOTTOM;
  }

  @Override
  public int getFitPosition() {
    return FIT_START;
  }

  @Override
  public int getXOffset() {
    return 0;
  }

  @Override
  public int getYOffset() {
    return 10;
  }
}
