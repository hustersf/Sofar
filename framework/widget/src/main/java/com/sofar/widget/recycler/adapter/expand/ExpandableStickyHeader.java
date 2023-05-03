package com.sofar.widget.recycler.adapter.expand;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class ExpandableStickyHeader extends FrameLayout {

  private StickyHeaderDecoration decoration = new StickyHeaderDecoration();
  private View headerView;

  public ExpandableStickyHeader(@NonNull Context context) {
    this(context, null);
  }

  public ExpandableStickyHeader(@NonNull Context context,
    @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ExpandableStickyHeader(@NonNull Context context, @Nullable AttributeSet attrs,
    int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public void onViewAdded(View child) {
    super.onViewAdded(child);
    if (child instanceof RecyclerView) {
      ((RecyclerView) child).addItemDecoration(decoration);
    }
  }

  @Override
  public void onViewRemoved(View child) {
    super.onViewRemoved(child);
    if (child instanceof RecyclerView) {
      ((RecyclerView) child).removeItemDecoration(decoration);
    }
  }

  private void showHeader(@NonNull View itemView, float y) {
    if (headerView == itemView) {
      headerView.setY(y);
      return;
    }

    if (headerView != null) {
      removeView(headerView);
    }
    this.headerView = itemView;
    addView(headerView);
    headerView.setY(y);
  }

  private class StickyHeaderDecoration extends RecyclerView.ItemDecoration {

    private ExpandableViewHolder headerViewHolder;
    private int headerType = -1;

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent,
      @NonNull RecyclerView.State state) {
      super.onDraw(c, parent, state);
      View firstChild = parent.getChildAt(0);
      if (firstChild == null) {
        return;
      }

      ExpandableAdapter adapter = (ExpandableAdapter) parent.getAdapter();
      ExpandableViewHolder holder = (ExpandableViewHolder) parent.getChildViewHolder(firstChild);
      int firstGroup = holder.groupPosition;
      int groupType = adapter.getGroupItemViewType(firstGroup);
      if (headerViewHolder == null || headerType != groupType) {
        headerViewHolder = adapter.onlyCreateViewHolder(parent, groupType);
        headerType = groupType;
      }

      if (headerViewHolder == null) {
        return;
      }

      int position = adapter.getGroupAdapterPosition(firstGroup);
      adapter.onBindViewHolder(headerViewHolder, position);

      View nextGroupView = findGroupItemView(firstGroup + 1, adapter, parent);
      float y = 0;
      if (nextGroupView != null) {
        y = nextGroupView.getY() - headerViewHolder.itemView.getHeight();
      }
      if (y > 0) {
        y = 0;
      }

      ExpandableStickyHeader.this.showHeader(headerViewHolder.itemView, y);
    }

    private View findGroupItemView(int groupPosition, ExpandableAdapter adapter,
      RecyclerView recyclerView) {
      for (int i = 0; i < recyclerView.getChildCount(); i++) {
        View child = recyclerView.getChildAt(i);
        if (child == null) {
          continue;
        }
        ExpandableViewHolder holder = (ExpandableViewHolder) recyclerView.getChildViewHolder(child);
        if (!adapter.isGroup(holder.getItemViewType())) {
          continue;
        }
        if (groupPosition == holder.groupPosition) {
          return holder.itemView;
        }
      }
      return null;
    }

  }

}


