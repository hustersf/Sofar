package com.sofar.widget.highlight;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sofar.R;

public class BottomComponent implements Component {

  View.OnClickListener listener;

  @NonNull
  String title;

  public BottomComponent() {
    this.title = "我是下方引导";
  }

  public BottomComponent(@NonNull String title) {
    this.title = title;
  }

  @Override
  public View getView(LayoutInflater inflater) {
    View view = inflater.inflate(R.layout.highlight_bottom_guide, null);
    view.setOnClickListener(v -> {
      if (listener != null) {
        listener.onClick(view);
      }
    });
    TextView next = view.findViewById(R.id.next);
    next.setText(title);
    return view;
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

  public void setOnClickListener(View.OnClickListener listener) {
    this.listener = listener;
  }

}
