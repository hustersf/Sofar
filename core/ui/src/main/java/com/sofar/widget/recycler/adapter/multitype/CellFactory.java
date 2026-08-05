package com.sofar.widget.recycler.adapter.multitype;

import androidx.annotation.NonNull;

import com.sofar.widget.recycler.adapter.Cell;

public interface CellFactory<T> {

  @NonNull
  Cell<T> onCreateCell();

}
