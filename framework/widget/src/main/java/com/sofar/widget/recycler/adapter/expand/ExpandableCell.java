package com.sofar.widget.recycler.adapter.expand;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;

public abstract class ExpandableCell<GROUP, CHILD> {

  public ExpandableViewHolder mViewHolder;

  protected abstract View createView(@NonNull ViewGroup parent);

  protected void onCreate(@NonNull View rootView) {}

  protected void onBindGroup(@NonNull GROUP group, boolean expand) {}

  protected boolean onInterceptGroupClick(@NonNull GROUP group, boolean expand) {
    return false;
  }

  protected void onBindChild(@NonNull CHILD child, boolean expand) {}

  protected void onUnbind() {}

  protected void onDestroy() {}

  protected void onViewAttached() {}

  protected void onViewDetached() {}

  public int getPosition() {
    return mViewHolder.getAdapterPosition();
  }
}
