package com.sofar.widget.recycler.adapter;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public final class CellViewHolder<T> extends RecyclerView.ViewHolder {

  public final Cell mCell;
  private boolean mBind;

  public CellViewHolder(@NonNull View itemView, @NonNull Cell cell) {
    super(itemView);
    mCell = cell;
    mCell.mViewHolder = this;
    mCell.onCreate(itemView);
  }

  public void bind(T data) {
    mCell.onBind(data);
    mBind = true;
  }

  public void unbind() {
    if (mBind) {
      mCell.onUnbind();
    }
    mBind = false;
  }

}
