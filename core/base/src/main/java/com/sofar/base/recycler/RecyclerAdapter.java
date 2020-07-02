package com.sofar.base.recycler;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.sofar.base.viewbinder.RecyclerViewBinder;
import com.sofar.base.viewbinder.ViewBinder;
import com.sofar.utility.CollectionUtil;
import com.sofar.utility.ViewUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @param <T> 列表数据的实体类
 *            封装一个通用的RecyclerView的适配器
 */
public abstract class RecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> {

  protected List<T> items;
  private boolean wrapped;

  private Set<ViewBinder> viewBinders = new LinkedHashSet<>();

  public RecyclerAdapter() {
    this(new ArrayList<>());
  }

  public RecyclerAdapter(List<T> datas) {
    items = datas;
  }

  public void setList(List<T> datas) {
    if (items == null) {
      items = new ArrayList<>();
    }
    items.clear();
    items.addAll(datas);
  }

  public void setListWithRelated(List<T> datas) {
    items = datas;
  }

  public List<T> getList() {
    return items;
  }

  @Nullable
  public T getItem(int position) {
    return (position < 0 || position >= items.size()) ? null : items.get(position);
  }

  @Override
  public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = ViewUtil.inflate(parent, getItemLayoutId(viewType), false);
    RecyclerViewBinder viewBinder = onCreateViewBinder(viewType);
    viewBinders.add(viewBinder);
    return new RecyclerViewHolder(view, viewBinder);
  }

  @Override
  public void onBindViewHolder(RecyclerViewHolder holder, int position) {
    int realPosition;
    if (wrapped) {
      realPosition = position;
    } else {
      realPosition = holder.getAdapterPosition();
    }
    holder.setViewAdapterPosition(realPosition);
    holder.onBindData(items.get(realPosition));
  }

  @Override
  public void onViewRecycled(RecyclerViewHolder holder) {
    super.onViewRecycled(holder);
  }

  @Override
  public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
    super.onDetachedFromRecyclerView(recyclerView);
    for (ViewBinder viewBinder : viewBinders) {
      viewBinder.destroy();
    }
  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  public boolean isEmpty() {
    return CollectionUtil.isEmpty(items);
  }

  /**
   * 子类提供布局id
   */
  protected abstract int getItemLayoutId(int viewType);

  /**
   * 子类创建具体的ViewBinder
   */
  @NonNull
  protected abstract RecyclerViewBinder onCreateViewBinder(int viewType);

  /**
   * @param wrapped 是否被{@link}
   */
  public void setWrappedByHeaderFooterAdapter(boolean wrapped) {
    this.wrapped = wrapped;
  }

}
