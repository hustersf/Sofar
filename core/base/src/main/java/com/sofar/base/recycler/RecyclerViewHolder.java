package com.sofar.base.recycler;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sofar.base.viewbinder.RecyclerViewBinder;

public class RecyclerViewHolder<T> extends RecyclerView.ViewHolder {

  @NonNull
  public RecyclerViewBinder viewBinder;

  public RecyclerViewHolder(@NonNull View itemView, @NonNull RecyclerViewBinder viewBinder) {
    super(itemView);
    this.viewBinder = viewBinder;
    viewBinder.create(itemView);
  }

  protected void onBindData(T item) {
    viewBinder.bind(item);
  }

  public void setViewAdapterPosition(int position) {
    viewBinder.setViewAdapterPosition(position);
  }
}
