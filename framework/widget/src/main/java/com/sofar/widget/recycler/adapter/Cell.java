package com.sofar.widget.recycler.adapter;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class Cell<T> {

  public RecyclerView.ViewHolder mViewHolder;

  protected abstract View createView(@NonNull ViewGroup parent);

  protected void onCreate(@NonNull View rootView) {}

  protected void onBind(@NonNull T data) {}

  protected void onUnbind() {}

  protected void onDestroy() {}

  protected void onViewAttached() {}

  protected void onViewDetached() {}

  public int getPosition() {
    return mViewHolder.getAdapterPosition();
  }
}
