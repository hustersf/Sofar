package com.sofar.widget.recycler.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public abstract class CellAdapter<T> extends RecyclerView.Adapter<CellViewHolder> {

  private List<Cell> mCells = new ArrayList<>();
  private List<T> mList = new ArrayList<>();

  public void setItems(@NonNull List<T> data) {
    mList.clear();
    mList.addAll(data);
  }

  @NonNull
  protected abstract Cell<T> onCreateCell(int viewType);

  @NonNull
  @Override
  public CellViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Cell cell = onCreateCell(viewType);
    View itemView = cell.createView(parent);
    mCells.add(cell);
    return new CellViewHolder(itemView, cell);
  }

  @Override
  public void onBindViewHolder(@NonNull CellViewHolder holder, int position) {
    T data = getItem(position);
    holder.bind(data);
  }

  public List<T> getItems() {
    return mList;
  }

  @Nullable
  public T getItem(int position) {
    return (position < 0 || position >= mList.size()) ? null : mList.get(position);
  }

  @Override
  public int getItemCount() {
    return mList.size();
  }

  @Override
  public void onViewRecycled(@NonNull CellViewHolder holder) {
    super.onViewRecycled(holder);
    holder.unbind();
  }

  @Override
  public void onViewAttachedToWindow(@NonNull CellViewHolder holder) {
    super.onViewAttachedToWindow(holder);
    holder.mCell.onViewAttached();
  }

  @Override
  public void onViewDetachedFromWindow(@NonNull CellViewHolder holder) {
    super.onViewDetachedFromWindow(holder);
    holder.mCell.onViewDetached();
  }

  @Override
  public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
    super.onDetachedFromRecyclerView(recyclerView);
    destroyCells();
  }

  private void destroyCells() {
    for (Cell cell : mCells) {
      cell.onDestroy();
    }
    mCells.clear();
  }

}
