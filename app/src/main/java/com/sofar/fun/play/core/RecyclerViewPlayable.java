package com.sofar.fun.play.core;

import androidx.recyclerview.widget.RecyclerView;

public interface RecyclerViewPlayable extends Playable {

  boolean canPlay(RecyclerView.ViewHolder holder, int adapterPosition);

  float getViewShowRatio();

}
