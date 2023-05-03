package com.sofar.widget.recycler.adapter.expand;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public final class ExpandableViewHolder<GROUP, CHILD> extends RecyclerView.ViewHolder {

  public final ExpandableCell mCell;
  private boolean mBind;
  public int groupPosition;

  public ExpandableViewHolder(@NonNull View itemView, @NonNull ExpandableCell cell) {
    super(itemView);
    mCell = cell;
    mCell.mViewHolder = this;
    mCell.onCreate(itemView);
  }

  public void bindGroup(GROUP group, boolean expand) {
    mCell.onBindGroup(group, expand);
    mBind = true;
  }

  public void bindChild(CHILD child, boolean expand) {
    mCell.onBindChild(child, expand);
    mBind = true;
  }

  public void unbind() {
    if (mBind) {
      mCell.onUnbind();
    }
    mBind = false;
  }

}
