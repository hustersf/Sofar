package com.sofar.widget.recycler.adapter.multitype;

import java.util.List;

import androidx.annotation.NonNull;

import com.sofar.widget.recycler.adapter.Cell;
import com.sofar.widget.recycler.adapter.CellAdapter;

/**
 * 支持不同数据类型的列表
 */
public class MultiTypeAdapter extends CellAdapter {

  private MultiTypes mTypes = new MultiTypes();

  /**
   * 一对一关系
   *
   * @param clazz   数据类型
   * @param factory 一种数据类型对应一种 factory
   */
  public <T> void register(Class<T> clazz, CellFactory<T> factory) {
    mTypes.register(new Type(clazz, factory, new DefaultLinker()));
  }

  /**
   * 一对多关系
   *
   * @param clazz     数据类型
   * @param factories 一种数据类型对应多个
   * @param linker    根据数据从 factories 中找出相应的 factory
   */
  public <T> void register(Class<T> clazz, List<CellFactory<T>> factories, Linker<T> linker) {
    for (CellFactory factory : factories) {
      mTypes.register(new Type(clazz, factory, linker));
    }
  }

  @NonNull
  @Override
  protected Cell onCreateCell(int viewType) {
    return mTypes.getType(viewType).mFactory.onCreateCell();
  }

  @Override
  public int getItemViewType(int position) {
    return indexOfType(position);
  }

  private int indexOfType(int position) {
    Object item = getItem(position);
    int index = mTypes.firstIndex(item.getClass());
    if (index != -1) {
      Linker linker = mTypes.getType(index).mLinker;
      return index + linker.index(position, item);
    }
    throw new IllegalStateException("you must register class=" + item.getClass());
  }
}
