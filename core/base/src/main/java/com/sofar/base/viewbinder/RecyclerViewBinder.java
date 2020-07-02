package com.sofar.base.viewbinder;

public class RecyclerViewBinder<T> extends ViewBinder<T> {

  public int viewAdapterPosition;

  public void setViewAdapterPosition(int position) {
    for (ViewBinder viewBinder : viewBinders) {
      if (viewBinder instanceof RecyclerViewBinder) {
        ((RecyclerViewBinder) viewBinder).setViewAdapterPosition(position);
      }
    }
    this.viewAdapterPosition = position;
  }
}
