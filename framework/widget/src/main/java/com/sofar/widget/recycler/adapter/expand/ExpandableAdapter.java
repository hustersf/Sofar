package com.sofar.widget.recycler.adapter.expand;

import java.util.ArrayList;
import java.util.List;

import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class ExpandableAdapter<Group, Child>
  extends RecyclerView.Adapter<ExpandableViewHolder> {

  private List<ExpandableCell> mCells = new ArrayList<>();
  private List<Group> mList = new ArrayList<>();
  private SparseBooleanArray expandState = new SparseBooleanArray();
  private ItemPosition tempItemPosition = new ItemPosition();

  public void setGroups(@NonNull List<Group> data) {
    mList.clear();
    mList.addAll(data);
  }

  public List<Group> getGroups() {
    return mList;
  }

  protected abstract ExpandableCell onCreateGroupCell(int viewType);

  protected abstract ExpandableCell onCreateChildCell(int viewType);

  @NonNull
  private ExpandableCell onCreateCell(int viewType) {
    if (isGroup(viewType)) {
      return onCreateGroupCell(viewType);
    }
    return onCreateChildCell(viewType);
  }

  @NonNull
  @Override
  public ExpandableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ExpandableCell cell = onCreateCell(viewType);
    View itemView = cell.createView(parent);
    mCells.add(cell);
    return new ExpandableViewHolder(itemView, cell);
  }

  /**
   * 仅仅只是创建一个 ExpandableViewHolder 对象，不放入 mCells 列表中
   */
  public ExpandableViewHolder onlyCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ExpandableCell cell = onCreateCell(viewType);
    View itemView = cell.createView(parent);
    return new ExpandableViewHolder(itemView, cell);
  }

  @Override
  public void onBindViewHolder(@NonNull ExpandableViewHolder holder, int position) {
    ItemPosition itemPosition = getAdapterItemPosition(position);
    boolean expand = isExpand(itemPosition.groupPosition);
    holder.groupPosition = itemPosition.groupPosition;
    if (itemPosition.childPosition == RecyclerView.NO_POSITION) {
      performGroupClick(holder, itemPosition.groupPosition);
      Group group = getGroupItem(itemPosition.groupPosition);
      holder.bindGroup(group, expand);
    } else {
      Child child = getChildItem(itemPosition.groupPosition, itemPosition.childPosition);
      holder.bindChild(child, expand);
    }
  }

  private void performGroupClick(@NonNull ExpandableViewHolder holder, int groupPosition) {
    holder.itemView.setOnClickListener(v -> {
      if (isExpand(groupPosition)) {
        collapse(groupPosition);
      } else {
        expand(groupPosition);
      }
    });
  }

  @Override
  public void onViewRecycled(@NonNull ExpandableViewHolder holder) {
    super.onViewRecycled(holder);
    holder.unbind();
  }

  @Override
  public void onViewAttachedToWindow(@NonNull ExpandableViewHolder holder) {
    super.onViewAttachedToWindow(holder);
    holder.mCell.onViewAttached();
  }

  @Override
  public void onViewDetachedFromWindow(@NonNull ExpandableViewHolder holder) {
    super.onViewDetachedFromWindow(holder);
    holder.mCell.onViewDetached();
  }

  @Override
  public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
    super.onDetachedFromRecyclerView(recyclerView);
    destroyCells();
  }

  private void destroyCells() {
    for (ExpandableCell cell : mCells) {
      cell.onDestroy();
    }
    mCells.clear();
  }

  @Override
  public final int getItemViewType(int position) {
    ItemPosition itemPosition = getAdapterItemPosition(position);
    if (itemPosition.childPosition == RecyclerView.NO_POSITION) {
      return getGroupItemViewType(itemPosition.groupPosition);
    }
    return getChildItemViewType(itemPosition.groupPosition, itemPosition.childPosition);
  }

  public int getGroupItemViewType(int groupPosition) {
    return 1;
  }

  public int getChildItemViewType(int groupPosition, int childPosition) {
    return -1;
  }

  protected boolean isGroup(int viewType) {
    return viewType > 0;
  }

  public boolean isExpand(int groupPosition) {
    return expandState.get(groupPosition);
  }

  private void setExpand(int groupPosition, boolean expand) {
    expandState.put(groupPosition, expand);
  }

  public void expand(int groupPosition) {
    setExpand(groupPosition, true);
    int start = getGroupAdapterPosition(groupPosition) + 1;
    notifyItemChanged(start - 1);
    notifyItemRangeInserted(start, getChildCount(groupPosition));
  }

  public void collapse(int groupPosition) {
    setExpand(groupPosition, false);
    int start = getGroupAdapterPosition(groupPosition) + 1;
    notifyItemChanged(start - 1);
    notifyItemRangeRemoved(start, getChildCount(groupPosition));
  }

  public void expandAll() {
    for (int i = 0; i < getGroupCount(); i++) {
      expandState.put(i, true);
    }
    notifyDataSetChanged();
  }

  public void collapseAll() {
    for (int i = 0; i < getGroupCount(); i++) {
      expandState.put(i, false);
    }
    notifyDataSetChanged();
  }

  public int getGroupAdapterPosition(int groupPosition) {
    int position = groupPosition;
    for (int i = 0; i < groupPosition; i++) {
      if (isExpand(i)) {
        position += getChildCount(i);
      }
    }
    return position;
  }

  public int getChildAdapterPosition(int groupPosition, int childPosition) {
    int childCount = getChildCount(groupPosition);
    if (!isExpand(groupPosition) || childCount <= 0) {
      return RecyclerView.NO_POSITION;
    }
    return getGroupAdapterPosition(groupPosition) + 1 + childPosition;
  }

  @Override
  public int getItemCount() {
    int itemCount = 0;
    int groupCount = getGroupCount();
    for (int i = 0; i < groupCount; i++) {
      itemCount++;
      if (isExpand(i)) {
        itemCount += getChildCount(i);
      }
    }
    return itemCount;
  }

  public int getGroupCount() {
    return mList.size();
  }

  public abstract int getChildCount(int groupPosition);

  public Group getGroupItem(int groupPosition) {
    return getGroups().get(groupPosition);
  }

  public abstract Child getChildItem(int groupPosition, int childPosition);

  private ItemPosition getAdapterItemPosition(int adapterPosition) {
    tempItemPosition.groupPosition = RecyclerView.NO_POSITION;
    tempItemPosition.childPosition = RecyclerView.NO_POSITION;

    int position = -1;
    for (int g = 0; g < getGroupCount(); g++) {
      position++;
      if (position == adapterPosition) {
        tempItemPosition.groupPosition = g;
        tempItemPosition.childPosition = RecyclerView.NO_POSITION;
        return tempItemPosition;
      }

      if (!isExpand(g)) {
        continue;
      }

      for (int c = 0; c < getChildCount(g); c++) {
        position++;
        if (position == adapterPosition) {
          tempItemPosition.groupPosition = g;
          tempItemPosition.childPosition = c;
          return tempItemPosition;
        }
      }
    }
    return tempItemPosition;
  }

  static class ItemPosition {
    int groupPosition;
    int childPosition = RecyclerView.NO_POSITION;
  }
}
