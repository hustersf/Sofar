package com.sofar.widget.recycler.adapter.expand;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class ExpandableStickyHeader extends FrameLayout {

  private static final String TAG = "ExpandableStickyHeader";

  private StickyHeaderDecoration decoration = new StickyHeaderDecoration();
  private View headerView;
  private boolean colorEnable = false;
  private int color = Color.TRANSPARENT;

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
      headerView.setVisibility(VISIBLE);
      headerView.setY(y);
      return;
    }

    if (headerView != null) {
      removeView(headerView);
    }
    this.headerView = itemView;
    addView(headerView);
    if (colorEnable) {
      headerView.setBackgroundColor(color);
    }
    headerView.setY(y);
  }

  private void hideHeader() {
    if (headerView != null) {
      headerView.setVisibility(GONE);
    }
  }

  public void setHeaderBackground(int color) {
    this.color = color;
    colorEnable = true;
  }

  public void reset() {
    colorEnable = false;
    decoration.headerViewHolder = null;
    removeView(headerView);
  }

  private class StickyHeaderDecoration extends RecyclerView.ItemDecoration {

    private ExpandableViewHolder headerViewHolder;
    private int headerType = -1;
    private int headGroup = -1;

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
      if (!adapter.isExpand(firstGroup)) {
        hideHeader();
        return;
      }
      int groupType = adapter.getGroupItemViewType(firstGroup);
      if (headerViewHolder == null || headerType != groupType) {
        headerViewHolder = adapter.onlyCreateViewHolder(parent, groupType);
        headerType = groupType;
        headGroup = -1;
      }

      if (headerViewHolder == null) {
        return;
      }

      if (headGroup != firstGroup) {
        int position = adapter.getGroupAdapterPosition(firstGroup);
        adapter.onBindViewHolder(headerViewHolder, position);
        headGroup = firstGroup;
      }

      float y = holder.itemView.getTop();
      if (y < 0) {
        y = 0;
      }
      showHeader(headerViewHolder.itemView, y);
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


