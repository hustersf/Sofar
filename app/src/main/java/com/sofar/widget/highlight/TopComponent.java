package com.sofar.widget.highlight;

import android.view.LayoutInflater;
import android.view.View;

import com.sofar.R;

public class TopComponent implements Component {

  @Override
  public View getView(LayoutInflater inflater) {
    return inflater.inflate(R.layout.highlight_top_guide, null);
  }

  @Override
  public int getAnchor() {
    return ANCHOR_TOP;
  }

  @Override
  public int getFitPosition() {
    return FIT_CENTER;
  }

  @Override
  public int getXOffset() {
    return 0;
  }

  @Override
  public int getYOffset() {
    return -10;
  }
}
